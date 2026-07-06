package com.reqai.backend.service;

import com.reqai.backend.entity.Document;
import com.reqai.backend.entity.OutboxEvent;
import com.reqai.backend.entity.OutboxStatus;
import com.reqai.backend.repository.DocumentRepository;
import com.reqai.backend.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequirementAnalysisConsumer {

    private final DocumentRepository documentRepository;
    private final OutboxEventRepository outboxEventRepository;

    @KafkaListener(topics = "document-analysis-topic",groupId = "reqai-group")
    @Transactional
    public void consumeDocumentEvent(String documentIdStr) {
        log.info("We catch new message from kafka.Operation is starting.Document id : {}", documentIdStr);

        UUID documentId = null;
        OutboxEvent event = null;
        try {
            documentId = UUID.fromString(documentIdStr.replace("\"", ""));

            // get document from database
            UUID finalDocumentId = documentId;
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document could not found : " + finalDocumentId));

            log.info("The document was  successfully read: {}. Content length: {} charachter",
                    document.getFileName(), document.getContent().length());

            event = outboxEventRepository.findByDocumentId(documentId)
                    .orElseThrow();

            event.setStatus(OutboxStatus.PROCESSED);

        } catch (Exception e) {
            log.error("Error : {}", e.getMessage());
            event.setStatus(OutboxStatus.FAILED);
        } finally {
            outboxEventRepository.save(event);
        }


        log.info("Finished processing for document: {}. Outbox status update attempted.", documentId);
    }
}
