package com.reqai.backend.dto;

import java.util.List;

public record AiRequirement(
        String description,
        String priority,
        String complexity,
        List<AiTask> tasks
){}
