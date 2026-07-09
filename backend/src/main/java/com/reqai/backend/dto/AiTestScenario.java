package com.reqai.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiTestScenario(
        String description,
        String expectedResult
){}
