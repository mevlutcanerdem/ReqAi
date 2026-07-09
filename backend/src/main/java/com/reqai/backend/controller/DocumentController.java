package com.reqai.backend.controller;

import com.reqai.backend.dto.DocumentDetailDto;
import com.reqai.backend.dto.DocumentSummaryDto;
import com.reqai.backend.entity.Document;
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

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "1. Document Controller " , description = "Uploading file and AI analyzing transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class DocumentController {

    private final DocumentService documentService;
    private final SseService sseService;

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a TXT file",description = "Customer upload requirement document and it starts the analyzing process on the back side")
    public ResponseEntity<Document> uploadDocument(@RequestParam("file")MultipartFile file) throws IOException {
        // 1.Step : just save file to database and take its ID
        Document savedDoc = documentService.saveFileOnly(file);

        // 2. Step : throw AI analysis to back side (fire and forget)
        documentService.startAsyncAnalysis(savedDoc.getId(),savedDoc);

        // return response to costumer
        return ResponseEntity.accepted().body(savedDoc);
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

    @GetMapping
    @Operation(summary = "List all documents",description = "Returns a list of all documents with their basic info.")
    public ResponseEntity<List<DocumentSummaryDto>> getAllDocuments(){
        List<DocumentSummaryDto> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

}
