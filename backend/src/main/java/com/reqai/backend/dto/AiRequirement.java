package com.reqai.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.security.SecureRandom;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiRequirement(

        String description,
        String priority,
        String complexity,
        List<AiTask> tasks
){}
