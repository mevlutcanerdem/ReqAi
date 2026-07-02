package com.reqai.backend.controller;

import com.reqai.backend.entity.Document;
import com.reqai.backend.service.DocumentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> uploadDocument(@RequestParam("file")MultipartFile file){
        try {
            Document savedDocument = documentService.saveDocument(file);
            return ResponseEntity.ok(savedDocument); // 200
        }catch (IOException e){
            return ResponseEntity.internalServerError().build(); // 500
        }

    }



}
