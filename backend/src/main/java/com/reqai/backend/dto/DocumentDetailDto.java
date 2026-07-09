package com.reqai.backend.dto;

import java.util.List;
import java.util.UUID;

public record DocumentDetailDto(
        UUID documentId,
        String fileName,
        String originalContent,
        List<AiRequirement> analysisResult
) {
}
