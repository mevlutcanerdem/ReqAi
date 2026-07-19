package com.reqai.backend.controller;

import com.reqai.backend.dto.DocumentDetailDto;
import com.reqai.backend.dto.DocumentSummaryDto;
import com.reqai.backend.entity.Document;
import com.reqai.backend.repository.DocumentRepository;
import com.reqai.backend.service.DocumentService;
import com.reqai.backend.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.print.Doc;
import java.io.DataInput;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "1. Document Controller " , description = "Uploading file and AI analyzing transactions")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final SseService sseService;
    private final DocumentRepository documentRepository;

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a TXT file",description = "Customer upload requirement document and it starts the analyzing process on the back side")
    public ResponseEntity<DocumentSummaryDto> uploadDocument(@RequestParam("file")MultipartFile file) throws IOException {
        // 1.Step : just save file to database and take its ID
        Document savedDoc = documentService.saveFileOnly(file);

        // 2. Step : throw AI analysis to back side (fire and forget)
        documentService.startAsyncAnalysis(savedDoc.getId(),savedDoc);

        // Entity yerine DTO dön - LazyInitializationException'ı tamamen engeller
        DocumentSummaryDto dto = new DocumentSummaryDto(savedDoc.getId(), savedDoc.getFileName(), savedDoc.getCreatedAt());
        return ResponseEntity.accepted().body(dto);
    }
    @GetMapping(value = "/stream/{documentId}")
    public SseEmitter streamEvents(@PathVariable String documentId){
        // They come in with a customer ID, and our call center connects them to a 10-minute line and hands it over
        return sseService.createEmitter(documentId);
    }


    @GetMapping("/{id}/analysis")
    @Operation(summary = "Get the document analysis details",description = "Returns original content and AI-generated hiyerarchy.")
    public ResponseEntity<DocumentDetailDto> getDocumentAnalysis(@PathVariable UUID id){
        DocumentDetailDto detail = documentService.getDocumentDetails(id);
        return ResponseEntity.ok(detail);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a document", description = "Deletes the document and its analysis results")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List all documents",description = "Returns a list of all documents with their basic info.")
    public ResponseEntity<List<DocumentSummaryDto>> getAllDocuments(){
        List<DocumentSummaryDto> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }
    @GetMapping("/history")
    public ResponseEntity<List<Document>> getUserDocument(Principal principal){
        // principal security i geçmiş ve içeri girmiş kullanııcnın kimlik kartıdır

        // 1 read username from card
        String currentUsername = principal.getName();

        // 2. Get the document just he has
        List<Document> userDocuments = documentRepository.findByUser_Username(currentUsername);

        // send it to frontend safely
        return ResponseEntity.ok(userDocuments);

    }

}
