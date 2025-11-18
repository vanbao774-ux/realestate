package com.example.realestate.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record NewsRequest(
    @NotBlank String title,
    @NotBlank String slug,
    String summary,
    String content,
    String coverUrl,
    Instant publishedAt,
    boolean published
) {
}
