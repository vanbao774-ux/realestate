package com.example.realestate.posts.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public record PostResponse(
    Long id,
    String title,
    String description,
    String purpose,
    String status,
    BigDecimal price,
    BigDecimal area,
    Integer bedrooms,
    Integer bathrooms,
    String propertyType,
    String province,
    String district,
    String ward,
    String address,
    BigDecimal latitude,
    BigDecimal longitude,
    String legalStatus,
    String orientation,
    String furnishingLevel,
    long views,
    Instant createdAt,
    Instant updatedAt,
    Instant expiresAt,
    String rejectReason,
    Instant featuredUntil,
    Set<String> amenities,
    List<String> mediaUrls
) {
}
