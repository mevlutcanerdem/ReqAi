package com.reqai.backend.service;

import com.reqai.backend.dto.*;
import com.reqai.backend.entity.Document;
import com.reqai.backend.entity.OutboxEvent;
import com.reqai.backend.entity.OutboxStatus;
import com.reqai.backend.entity.Requirement;
import com.reqai.backend.entity.User; // EKLENDİ
import com.reqai.backend.repository.DocumentRepository;
import com.reqai.backend.repository.OutboxEventRepository;
import com.reqai.backend.repository.RequirementRepository;
import com.reqai.backend.repository.UserRepository; // EKLENDİ
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder; // EKLENDİ
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
    private final UserRepository userRepository; // 1. EKLENDİ

    // Constructor güncellendi
    public DocumentService(DocumentRepository documentRepository, OutboxEventRepository outboxEventRepository, SseService sseService, OpenAiService openAiService, RequirementRepository requirementRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.sseService = sseService;
        this.openAiService = openAiService;
        this.requirementRepository = requirementRepository;
        this.userRepository = userRepository; // 2. EKLENDİ
    }

    // main thread
    @Transactional
    // @CachePut(value = "documents", key = "#result.id")
    public Document saveFileOnly(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty!");
        }
        System.out.println("Size of the uploaded file: " + file.getSize() + " bytes");

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        // 3. Güvenlik kapısından (JWT) geçmiş kullanıcının adını yakala
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 4. Bu ismi veritabanından bulup gerçek User objesini çek
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // save document to database and create an id
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setContent(content);
        document.setUser(currentUser); // 5. İŞTE 500 HATASINI ÇÖZEN O KRİTİK SATIR!

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
            AiAnalysisResponse aiResult = openAiService.analyzeAndSave(document);

            // we send json data turning from AI to the angular via sse
            sseService.sendEvent(documentId.toString(), aiResult);

            System.out.println("Analysis completed,It sent to angular from sse ");

        }catch (Exception e){
            sseService.sendEvent(documentId.toString(),"{ \"error\": \"An error occurred while analysing.\"}");

        }
    }

    public List<DocumentSummaryDto> getAllDocuments(){
        // 1. İsteği atan (token'ı olan) kullanıcının adını al
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        // we're retrieving the documents in reverse chronological order
        return documentRepository.findByUser_Username(currentUsername)
                .stream()
                // Tarihe göre tersten sıralama işlemi stream içinde yapılabilir
                .sorted((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()))
                .map(doc -> new DocumentSummaryDto(
                        doc.getId(),
                        doc.getFileName(),
                        doc.getCreatedAt()
                ))
                .toList();
    }

    // Redis implementation (Read from RAM )
    // when user send a request to this method spring asks redis before. if document exist this method does not work
    // @Cacheable(value = "documents",key = "#id")
    public Document getDocumentById(UUID id){
        // (2.İSTEKTE GÖRÜLMEZ BU YAZI ÇÜNKÜ İLK İSTEKTE REDİSTE YOKSA BİLE İKİNCİ İSTEKTE VERİTABANINA KAYDEDİLİR VE KOPYASI REDİSE KAYDEDİLİR
        System.out.println("VERİTABANINA İNİLDİ");

        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: "  + id));
    }

    @Transactional(readOnly = true)
    public DocumentDetailDto getDocumentDetails(UUID id){
        // 1. İsteği atan kullanıcının adını al
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. retrieve main document and original content
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Document not found"));
        if (!document.getUser().getUsername().equals(currentUsername)) {
            // Eğer başkasının belgesine ID ile erişmeye çalışırsa 403 Forbidden fırlat
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu belgeye erişim yetkiniz yok.");

        }
        // 4. İzinler tamamsa detayları çek ve DTO'ya çevir
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