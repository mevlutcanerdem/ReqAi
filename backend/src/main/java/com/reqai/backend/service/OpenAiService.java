package com.reqai.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reqai.backend.dto.AiAnalysisResponse;
import com.reqai.backend.entity.Document;
import com.reqai.backend.entity.Requirement;
import com.reqai.backend.entity.Task;
import com.reqai.backend.entity.TestScenario;
import com.reqai.backend.repository.RequirementRepository;
import com.reqai.backend.repository.TaskRepository;
import com.reqai.backend.repository.TestScenarioRepository;
import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j; // --> SİLİNDİ
import org.slf4j.Logger;            // --> EKLENDİ
import org.slf4j.LoggerFactory;     // --> EKLENDİ
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    // Manuel Logger Tanımlaması (Lombok hatasını tamamen engeller)
    private static final Logger log = LoggerFactory.getLogger(OpenAiService.class);

    // Fetching configurations from application.yml
    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model}")
    private String model;

    // Repositories to interact with the database
    private final RequirementRepository requirementRepository;
    private final TaskRepository taskRepository;
    private final TestScenarioRepository testScenarioRepository; // Not: Orijinal kodunuzda bu satır repository ismiyle eşleşiyordu

    // ObjectMapper is used to convert JSON strings to Java Objects (and vice versa)
    private final ObjectMapper objectMapper = new ObjectMapper();

    // RestClient is the modern Spring Boot way to make HTTP requests to external APIs (like OpenAI)
    private final RestClient restClient = RestClient.create();

    @Transactional // Ensures that if any database save fails, the whole process rolls back
    public String analyzeAndSave(Document document) {
        log.info("Sending document to OpenAI for analysis. Document ID: {}", document.getId());

        // STEP 1: Define the System Prompt
        String systemPrompt = """
                You are an expert software analyst. Analyze the provided text and extract business requirements.
                You MUST respond strictly in the following JSON format. Do not include any conversational text.
                Format:
                {
                  "requirements": [
                    {
                      "title": "A short, clear title for the requirement",
                      "description": "Description of the business requirement",
                      "priority": "HIGH/MEDIUM/LOW",
                      "complexity": "HIGH/MEDIUM/LOW",
                      "tasks": [
                        {
                          "title": "A short, clear title for the task",
                          "description": "Developer task description",
                          "testScenarios": [
                            { "description": "Test scenario description",
                              "expectedResult": "Define what the expected outcome is"
                             }
                          ]
                        }
                      ]
                    }
                  ]
                }
                """;

        // STEP 2: Prepare the Request Body
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", "Document to analyze: \n" + document.getContent())
                )
        );

        try {
            // STEP 3: Make the HTTP POST Request
            String responseStr = restClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            // STEP 4: Extract the AI's Message
            JsonNode rootNode = objectMapper.readTree(responseStr);
            String aiJsonContent = rootNode.path("choices").get(0).path("message").path("content").asText();

            // STEP 5: Map JSON to Java Objects (DTOs)
            AiAnalysisResponse analysis = objectMapper.readValue(aiJsonContent, AiAnalysisResponse.class);

            // STEP 6: Save the structured data to our relational database
            saveAnalysisToDatabase(document, analysis);

            log.info("OpenAI Analysis successfully completed and saved to the database!");

            return aiJsonContent;

        } catch (Exception e) {
            log.error("Error occurred during OpenAI API call or database operation: {}", e.getMessage(), e);
            throw new RuntimeException("AI Analysis Failed", e);
        }

    }

    // Helper method to handle the hierarchical database inserts
    private void saveAnalysisToDatabase(Document document, AiAnalysisResponse analysis) {
        if (analysis.requirements() == null) return;

        for (var aiReq : analysis.requirements()) {
            Requirement req = new Requirement();

            req.setDocument(document);
            req.setDescription(aiReq.description());
            req.setPriority(aiReq.priority());
            req.setComplexity(aiReq.complexity());
            Requirement savedReq = requirementRepository.save(req);

            if (aiReq.tasks() == null) continue;
            for (var aiTask : aiReq.tasks()) {
                Task task = new Task();
                task.setTitle(aiTask.title());
                task.setRequirement(savedReq);
                task.setDescription(aiTask.description());
                Task savedTask = taskRepository.save(task);

                if (aiTask.testScenarios() == null) continue;
                for (var aiTest : aiTask.testScenarios()) {
                    TestScenario test = new TestScenario();
                    test.setExpectedResult(aiTest.expectedResult());
                    test.setTask(savedTask);
                    test.setDescription(aiTest.description());
                    // Not: Orijinal kodunuzda testScenarioRepository tipini en üstte doğrulamayı unutmayın
                    ((TestScenarioRepository) testScenarioRepository).save(test);
                }
            }
        }
    }
}
