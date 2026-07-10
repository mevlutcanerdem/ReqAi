package com.reqai.backend.controller;

import com.reqai.backend.dto.DocumentSummaryDto;
import com.reqai.backend.entity.Document;
import com.reqai.backend.service.DocumentService;
import com.reqai.backend.service.SseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

// webmvc kullanacağız bu bize browser veya tomcat gibi davranır ve controller testinde biz
// statu kodlarını 202,404 ve url e doğru bir http isteği get,post atabilior muyum onu test ederiz

// just get the DocumentController up , do not upload the other things like database etc.
@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    // mock postman . it provides sending request to system
    @Autowired
    private MockMvc mockMvc;

    // mock services that we will inject inside the controller
    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private SseService sseService;

    // Get the all documents (GET api/v1/documents)

    @Test
    void shouldReturnAllDocumentsAnd200Ok_WhenGetAllDocumentsEndpointIsCalled() throws Exception{
        //  GIVEN : we teach a rule to the mock service
        UUID docId = UUID.randomUUID();
        DocumentSummaryDto mockDto = new DocumentSummaryDto(docId,"test.txt", LocalDateTime.now());

        // when we call "getAllDocuments" return this list
        List<DocumentSummaryDto> mockList = List.of(mockDto);
        when(documentService.getAllDocuments()).thenReturn(mockList);

        //  when & then (submit a request and validate) : we submit a request via mock postman
        mockMvc.perform(get("/api/v1/documents"))
                .andExpect(status().isOk()) // it must return http 200
                .andExpect(jsonPath("$.size()").value(1))// The size of the upcoming json must be 1
                // the name of the first name element in the list must be "test.txt"
                .andExpect(jsonPath("$[0].fileName").value("test.txt"));

    }

    // File uploading (POST api/v1/documents/upload)
    @Test
    void shouldReturn202AcceptedAndDocument_WhenFileIsUploadedSuccessfully() throws Exception{

        // Given
        // 1. We're creating a mock file (like it's coming from UI)
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-requirements.txt",
                "text/plain",
                "This is a customer requirement.".getBytes()
        );

        // if we call "saveFileOnly" return a document
        Document mockSavedDoc = new Document();
        UUID generatedId = UUID.randomUUID();
        mockSavedDoc.setId(generatedId);
        mockSavedDoc.setFileName("test-requirements.txt");

        when(documentService.saveFileOnly(any(MultipartFile.class))).thenReturn(mockSavedDoc);

        // when & then : we submit a multipart request via mock postman
        mockMvc.perform(multipart("/api/v1/documents/upload")
                .file(mockFile)) // we add file to the request
                .andExpect(status().isAccepted()) // it must return 202 accepted
                .andExpect(jsonPath("$.id").value(generatedId.toString())) // returning json must include our id
                .andExpect(jsonPath("$.fileName").value("test-requirements.txt"));

            // verifying
            // we check the controller whether it forget the trigger the asynchronous method on background
            verify(documentService,times(1)).startAsyncAnalysis(generatedId,mockSavedDoc);


    }



}