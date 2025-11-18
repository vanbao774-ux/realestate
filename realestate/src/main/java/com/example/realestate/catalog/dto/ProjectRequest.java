package com.example.realestate.catalog.dto;

import com.example.realestate.catalog.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProjectRequest(
    @NotBlank String name,
    String investor,
    Long provinceId,
    Long districtId,
    Long wardId,
    String address,
    BigDecimal priceFrom,
    BigDecimal priceTo,
    @NotNull ProjectStatus status,
    BigDecimal lat,
    BigDecimal lng,
    String description,
    String coverUrl
) {
}
