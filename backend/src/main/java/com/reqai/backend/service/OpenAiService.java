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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

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
    private final TestScenarioRepository testScenarioRepository;

    // ObjectMapper is used to convert JSON strings to Java Objects (and vice versa)
    private final ObjectMapper objectMapper = new ObjectMapper();

    // RestClient is the modern Spring Boot way to make HTTP requests to external APIs (like OpenAI)
    private final RestClient restClient = RestClient.create();

    @Transactional // Ensures that if any database save fails, the whole process rolls back
    public void analyzeAndSave(Document document) {
        log.info("Sending document to OpenAI for analysis. Document ID: {}", document.getId());

        // STEP 1: Define the System Prompt
        // We give strict instructions to the AI to behave like a software analyst
        // and return ONLY a JSON structure. This prevents conversational responses.
        String systemPrompt = """
                You are an expert software analyst. Analyze the provided text and extract business requirements.
                You MUST respond strictly in the following JSON format. Do not include any conversational text.
                Format:
                {
                  "requirements": [
                    {
                      "description": "Description of the business requirement",
                      "priority": "HIGH/MEDIUM/LOW",
                      "complexity": "HIGH/MEDIUM/LOW",
                      "tasks": [
                        {
                          "description": "Developer task description",
                          "testScenarios": [
                            { "description": "Test scenario description" }
                          ]
                        }
                      ]
                    }
                  ]
                }
                """;

        // STEP 2: Prepare the Request Body
        // We construct the payload (package) that OpenAI's API expects.
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "response_format", Map.of("type", "json_object"), // Forces OpenAI to return valid JSON
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt), // Our instructions
                        Map.of("role", "user", "content", "Document to analyze: \n" + document.getContent()) // User's file content
                )
        );

        try {
            // STEP 3: Make the HTTP POST Request
            // We are knocking on OpenAI's door, showing our API key, and handing over the request body.
            String responseStr = restClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve() // Execute the request
                    .body(String.class); // Get the response as a raw String

            // STEP 4: Extract the AI's Message
            // The response has a lot of metadata. We navigate the JSON tree to find just the AI's reply content.
            JsonNode rootNode = objectMapper.readTree(responseStr);
            String aiJsonContent = rootNode.path("choices").get(0).path("message").path("content").asText();

            // STEP 5: Map JSON to Java Objects (DTOs)
            // We pour the raw JSON string into our predefined Java Record molds.
            AiAnalysisResponse analysis = objectMapper.readValue(aiJsonContent, AiAnalysisResponse.class);

            // STEP 6: Save the structured data to our relational database
            saveAnalysisToDatabase(document, analysis);

            log.info("OpenAI Analysis successfully completed and saved to the database!");

        } catch (Exception e) {
            log.error("Error occurred during OpenAI API call or database operation: {}", e.getMessage(), e);
            throw new RuntimeException("AI Analysis Failed", e);
        }
    }

    // Helper method to handle the hierarchical database inserts
    private void saveAnalysisToDatabase(Document document, AiAnalysisResponse analysis) {
        // If the AI didn't return any requirements, exit the method safely
        if (analysis.requirements() == null) return;

        // Iterate over each requirement returned by the AI
        for (var aiReq : analysis.requirements()) {

            // 1. Create and save the Requirement entity
            Requirement req = new Requirement();
            req.setDocument(document);
            req.setDescription(aiReq.description());
            req.setPriority(aiReq.priority());
            req.setComplexity(aiReq.complexity());
            Requirement savedReq = requirementRepository.save(req);

            // Iterate over tasks related to this specific requirement
            if (aiReq.tasks() == null) continue;
            for (var aiTask : aiReq.tasks()) {

                // 2. Create and save the Task entity, linking it to the saved Requirement's ID
                Task task = new Task();
                task.setRequirement(savedReq);
                task.setDescription(aiTask.description());
                Task savedTask = taskRepository.save(task);

                // Iterate over test scenarios related to this specific task
                if (aiTask.testScenarios() == null) continue;
                for (var aiTest : aiTask.testScenarios()) {

                    // 3. Create and save the TestScenario entity, linking it to the saved Task's ID
                    TestScenario test = new TestScenario();
                    test.setTask(savedTask);
                    test.setDescription(aiTest.description());
                    testScenarioRepository.save(test);
                }
            }
        }
    }
}