package com.example.realestate.posts.dto;

import java.math.BigDecimal;
import java.util.Optional;

public record PostFilter(
    Optional<String> keyword,
    Optional<String> purpose,
    Optional<Long> propertyTypeId,
    Optional<Long> provinceId,
    Optional<Long> districtId,
    Optional<Long> wardId,
    Optional<BigDecimal> priceMin,
    Optional<BigDecimal> priceMax,
    Optional<BigDecimal> areaMin,
    Optional<BigDecimal> areaMax,
    Optional<Integer> bedrooms,
    Optional<Integer> bathrooms,
    Optional<Long> orientationId,
    Optional<Long> furnishingLevelId,
    Optional<String> status,
    Optional<BigDecimal> latitude,
    Optional<BigDecimal> longitude,
    Optional<BigDecimal> radiusKm
) {
}
