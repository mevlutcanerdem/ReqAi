package com.reqai.backend.dto;

import java.util.List;

public record AiTask(
        String description,
        List<AiTestScenario> testScenarios
){}
