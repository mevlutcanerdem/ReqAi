package com.reqai.backend.service;


import com.reqai.backend.entity.Document;
import com.reqai.backend.entity.OutboxEvent;
import com.reqai.backend.entity.OutboxStatus;
import com.reqai.backend.repository.DocumentRepository;

import com.reqai.backend.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OutboxEventRepository outboxEventRepository;

    public DocumentService(DocumentRepository documentRepository, OutboxEventRepository outboxEventRepository) {
        this.documentRepository = documentRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
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
}
