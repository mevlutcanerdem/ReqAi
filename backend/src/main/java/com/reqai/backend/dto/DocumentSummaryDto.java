package com.reqai.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentSummaryDto(
    UUID id,
    String fileName,
    LocalDateTime uploadDate
){}

