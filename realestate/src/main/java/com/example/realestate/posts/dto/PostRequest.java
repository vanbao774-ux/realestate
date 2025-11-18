package com.example.realestate.posts.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;

public record PostRequest(
    @NotNull Long ownerId,
    @NotBlank @Size(max = 200) String title,
    @NotBlank String description,
    @NotBlank String purpose,
    @NotNull Long propertyTypeId,
    Long provinceId,
    Long districtId,
    Long wardId,
    String address,
    @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
    @DecimalMin(value = "0.0", inclusive = false) BigDecimal area,
    Integer bedrooms,
    Integer bathrooms,
    Long orientationId,
    Long furnishingLevelId,
    String legalStatus,
    BigDecimal latitude,
    BigDecimal longitude,
    Set<Long> amenityIds
) {
}
