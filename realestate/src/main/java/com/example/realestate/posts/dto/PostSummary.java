package com.example.realestate.posts.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PostSummary(
    Long id,
    String title,
    String purpose,
    BigDecimal price,
    BigDecimal area,
    String propertyType,
    String province,
    String district,
    String ward,
    Instant createdAt
) {
}
