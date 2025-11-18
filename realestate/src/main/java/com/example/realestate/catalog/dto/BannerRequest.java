package com.example.realestate.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public record BannerRequest(
    @NotBlank String position,
    @NotBlank String imageUrl,
    String targetUrl,
    boolean active,
    int sortOrder
) {
}
