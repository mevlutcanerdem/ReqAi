package com.reqai.backend.service;


import com.reqai.backend.dto.*;
import com.reqai.backend.entity.Document;
import com.reqai.backend.entity.OutboxEvent;
import com.reqai.backend.entity.OutboxStatus;
import com.reqai.backend.entity.Requirement;
import com.reqai.backend.repository.DocumentRepository;

import com.reqai.backend.repository.OutboxEventRepository;
import com.reqai.backend.repository.RequirementRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final SseService sseService;
    private final OpenAiService openAiService;
    private final RequirementRepository requirementRepository;

    public DocumentService(DocumentRepository documentRepository, OutboxEventRepository outboxEventRepository, SseService sseService, OpenAiService openAiService, RequirementRepository requirementRepository) {
        this.documentRepository = documentRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.sseService = sseService;
        this.openAiService = openAiService;
        this.requirementRepository = requirementRepository;
    }
        // main thread
    @Transactional
    @CachePut(value = "documents",key = "#result.id")
    public Document saveFileOnly(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty!");
        }
        System.out.println("Size of the uploaded file: " + file.getSize() + " bytes");

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        // save document to database    and create an id
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setContent(content);
        Document savedDocument = documentRepository.save(document);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setDocumentId(savedDocument.getId());
        outboxEvent.setStatus(OutboxStatus.PENDING);
        outboxEventRepository.save(outboxEvent);

        return savedDocument;
    }

   // BACKGROUND METHOD (Run by a Background Worker)
    @Async  // Tomcatin ana threadini meşgul etmeyen sihirli notasyon
    public void startAsyncAnalysis(UUID documentId,Document document){
        try {
            System.out.println("Background worker start analysis.Assigned thread : " + Thread.currentThread().getName());

            // we connect to OpenAPI
            // This process may result in around 5,15 second , main thread is free meanwhile
            String aiResultJson = openAiService.analyzeAndSave(document);

            // we send json data turning from AI to the angular via sse
            sseService.sendEvent(documentId.toString(),aiResultJson);

            System.out.println("Analysis completed,It sent to angular from sse ");

        }catch (Exception e){
            sseService.sendEvent(documentId.toString(),"{ \"error\": \"An error occurred while analysing.\"}");

        }
    }
    public List<DocumentSummaryDto> getAllDocuments(){
        // we're retrieving the documents in reverse chronological order
        return documentRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"))
                .stream()
                .map(doc -> new DocumentSummaryDto(
                        doc.getId(),
                        doc.getFileName(),
                        doc.getCreatedAt()
                ))
                .toList();
    }

    // Redis implementation (Read from RAM )
    // when user send a request to this method spring asks redis before. if document exist this method does not work
    @Cacheable(value = "documents",key = "#id")
    public Document getDocumentById(UUID id){
        // (2.İSTEKTE GÖRÜLMEZ BU YAZI ÇÜNKÜ İLK İSTEKTE REDİSTE YOKSA BİLE İKİNCİ İSTEKTE VERİTABANINA KAYDEDİLİR VE KOPYASI REDİSE KAYDEDİLİR
        System.out.println("VERİTABANINA İNİLDİ");

        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: "  + id));
    }

    public DocumentDetailDto getDocumentDetails(UUID id){

        // 1. retrieve main document and original content
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Document not found"));

        // 2. retrieve all requirements and sub relationships depend on this document
        List<Requirement> requirements = requirementRepository.findByDocumentId(document.getId());

        // convert the database entities to dto structure
        List<AiRequirement> aiRequirements = requirements.stream().map(req -> new AiRequirement(
                req.getDescription(),
                req.getPriority(),
                req.getComplexity(),
                req.getTasks().stream().map(task -> new AiTask(
                        task.getTitle(),
                        task.getDescription(),
                        task.getTestScenarios().stream().map(testScenario -> new AiTestScenario(
                                testScenario.getDescription(),
                                testScenario.getExpectedResult()
                        )).toList()
                )).toList()
        )).toList();

        // package all and return
        return new DocumentDetailDto(
                document.getId(),
                document.getFileName(),
                document.getContent(),
                aiRequirements
        );

    }
}
