package com.reqai.backend.service;
import com.reqai.backend.entity.Document;
import com.reqai.backend.entity.OutboxEvent;
import com.reqai.backend.repository.DocumentRepository;
import com.reqai.backend.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    // we are mocking it so it doesn't access the actual database
    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private OpenAiService openAiService;

    @Mock
    private SseService sseService;

    //The actual service we'll test
    @InjectMocks
    private DocumentService documentService;

    // EMPTY FILE SCENARİO (SAD PATH)
    @Test
    void shouldThrowException_WhenFileIsEmpty(){
        // we create a file fully empty
        MultipartFile emptyFile = new MockMultipartFile("file","empty.txt","text/plain",new byte[0]);

        // when we send this empty file to service we expect it throws IllegalArgumentException
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.saveFileOnly(emptyFile)
        );

        // Is the message of the thrown exception exactly the same as the message we wrote in our code?
        assertEquals("Uploaded file is empty!",exception.getMessage());

        // we need to ensure that it does not go to the databases
        verifyNoInteractions(documentRepository);
        verifyNoInteractions(outboxEventRepository);


    }
        // SUCCESSFULLY SAVING SCENARIO (HAPPY PATH)
    @Test
    void shouldSaveDocumentAndOutboxEvent_WhenFileIsValid() throws IOException{
        // we create a new valid file
        MultipartFile validFile = new MockMultipartFile("file","req.txt","text/plain","Giris yapabilmeli".getBytes());

        // we assign a rule to tha mock database "İf you get a document , return a thing containing ID"
        Document mockSaveDoc = new Document();
        mockSaveDoc.setId(UUID.randomUUID());
        when(documentRepository.save(any(Document.class))).thenReturn(mockSaveDoc);

        // WHEN : we run our real method
        Document result = documentService.saveFileOnly(validFile);

        // Then : Assertions & Verifications
        assertNotNull(result); // returning object must not be null
        assertEquals(mockSaveDoc.getId(),result.getId()); // Ids must be equal each other

        // Did our method actually call the `save` method of `documentRepository` once?
        verify(documentRepository).save(any(Document.class));

        // Did our method actually call the `save` method of `outboxEventRepository` once?
        verify(outboxEventRepository).save(any(OutboxEvent.class));

    }
    // Ai analizi başarılı olursa happy path
    @Test
    void shouldSendAiResultViaSse_WhenAnalysisSuccessfull(){
        // we have document id and document object
        UUID docId = UUID.randomUUID();
        Document mockDoc = new Document();
        com.reqai.backend.dto.AiAnalysisResponse fakeAiResponse = new com.reqai.backend.dto.AiAnalysisResponse(java.util.List.of());

        // kuralımız openaiService.analyzeAndSave(mockDock) çağırıldığında fakeAiResponse döndür.
        when(openAiService.analyzeAndSave(mockDoc)).thenReturn(fakeAiResponse);

        // call the method
        documentService.startAsyncAnalysis(docId,mockDoc);

        // then : SseService in sendEvent metodu docId.toString ve fakeAiResponse ile 1 kere çağırılmış olmalı
        verify(sseService,times(1)).sendEvent(docId.toString(),fakeAiResponse);

    }

    // if Ai service collapse (exception handling)
    @Test
    void shouldSendErrorJsonViaSse_WhenOpenAiThrowsException(){

        // we have document id
        UUID docId = UUID.randomUUID();
        Document mockDoc = new Document();

        // Rule : when we call openAiService.analyzeAndSave , throw RunTimeException
        when(openAiService.analyzeAndSave(mockDoc)).thenThrow(new RuntimeException("API limit exceed"));

        // when : methodu çağır
        documentService.startAsyncAnalysis(docId,mockDoc);

        // then : SSE service in sendEvent methodu , docId ve Hata Json u ile çağırılmış olmalı
        verify(sseService,times(1)).sendEvent(docId.toString(),"{ \"error\": \"An error occurred while analysing.\"}");


    }



}