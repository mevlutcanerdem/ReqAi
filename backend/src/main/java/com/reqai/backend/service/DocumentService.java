package com.reqai.backend.service;


import com.reqai.backend.entity.Document;
import com.reqai.backend.entity.OutboxEvent;
import com.reqai.backend.entity.OutboxStatus;
import com.reqai.backend.repository.DocumentRepository;

import com.reqai.backend.repository.OutboxEventRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OutboxEventRepository outboxEventRepository;

    public DocumentService(DocumentRepository documentRepository, OutboxEventRepository outboxEventRepository) {
        this.documentRepository = documentRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    @CachePut(value = "documents",key = "#result.id")
    public Document saveDocument(MultipartFile file) throws IOException{

        if(file.isEmpty()){
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        System.out.println("Size of the uploaded file : " + file.getSize() + " byte");

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

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

    // Redis implementation (Read from RAM )
    // when user send a request to this method spring asks redis before. if document exist this method does not work
    @Cacheable(value = "documents",key = "#id")
    public Document getDocumentById(UUID id){
        // (2.İSTEKTE GÖRÜLMEZ BU YAZI ÇÜNKÜ İLK İSTEKTE REDİSTE YOKSA BİLE İKİNCİ İSTEKTE VERİTABANINA KAYDEDİLİR VE KOPYASI REDİSE KAYDEDİLİR
        System.out.println("VERİTABANINA İNİLDİ");

        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: "  + id));
    }
}
