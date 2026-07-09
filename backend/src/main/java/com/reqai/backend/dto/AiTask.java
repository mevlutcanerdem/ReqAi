package com.reqai.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiTask(
        String title,
        String description,
        List<AiTestScenario> testScenarios
){}
