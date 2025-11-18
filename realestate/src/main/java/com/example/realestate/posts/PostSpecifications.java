package com.example.realestate.posts;

import com.example.realestate.posts.dto.PostFilter;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.realestate.posts.PostPurpose;

public final class PostSpecifications {

    private PostSpecifications() {
    }

    public static Specification<Post> withFilter(PostFilter filter) {
        return Specification.where(keyword(filter.keyword()))
            .and(enumEquals("purpose", filter.purpose()))
            .and(longEquals("propertyType", "id", filter.propertyTypeId()))
            .and(longEquals("province", "id", filter.provinceId()))
            .and(longEquals("district", "id", filter.districtId()))
            .and(longEquals("ward", "id", filter.wardId()))
            .and(range("price", filter.priceMin(), filter.priceMax()))
            .and(range("area", filter.areaMin(), filter.areaMax()))
            .and(integerEquals("bedrooms", filter.bedrooms()))
            .and(integerEquals("bathrooms", filter.bathrooms()))
            .and(longEquals("orientation", "id", filter.orientationId()))
            .and(longEquals("furnishingLevel", "id", filter.furnishingLevelId()))
            .and(enumStatusEquals(filter.status()))
            .and(withinRadius(filter.latitude(), filter.longitude(), filter.radiusKm()));
    }

    private static Specification<Post> keyword(Optional<String> keyword) {
        return keyword.filter(StringUtils::hasText)
            .map(value -> (Specification<Post>) (root, query, cb) -> {
                String pattern = "%" + value.toLowerCase() + "%";
                return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
                );
            })
            .orElse(null);
    }

    private static Specification<Post> enumEquals(String property, Optional<String> value) {
        return value.filter(StringUtils::hasText)
            .map(val -> (Specification<Post>) (root, query, cb) -> cb.equal(root.get(property), PostPurpose.valueOf(val.toUpperCase())))
            .orElse(null);
    }

    private static Specification<Post> enumStatusEquals(Optional<String> status) {
        return status.filter(StringUtils::hasText)
            .map(val -> (Specification<Post>) (root, query, cb) -> cb.equal(root.get("status"), PostStatus.valueOf(val.toUpperCase())))
            .orElse(null);
    }

    private static Specification<Post> longEquals(String association, String field, Optional<Long> value) {
        return value
            .map(val -> (Specification<Post>) (root, query, cb) -> cb.equal(root.join(association, JoinType.LEFT).get(field), val))
            .orElse(null);
    }

    private static Specification<Post> range(String property, Optional<BigDecimal> min, Optional<BigDecimal> max) {
        Specification<Post> spec = null;
        if (min.isPresent()) {
            BigDecimal minValue = min.get();
            spec = (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(property), minValue);
        }
        if (max.isPresent()) {
            BigDecimal maxValue = max.get();
            Specification<Post> maxSpec = (root, query, cb) -> cb.lessThanOrEqualTo(root.get(property), maxValue);
            spec = spec == null ? maxSpec : spec.and(maxSpec);
        }
        return spec;
    }

    private static Specification<Post> integerEquals(String property, Optional<Integer> value) {
        return value
            .map(val -> (Specification<Post>) (root, query, cb) -> cb.equal(root.get(property), val))
            .orElse(null);
    }

    private static Specification<Post> withinRadius(Optional<BigDecimal> latitude, Optional<BigDecimal> longitude, Optional<BigDecimal> radiusKm) {
        if (latitude.isEmpty() || longitude.isEmpty() || radiusKm.isEmpty()) {
            return null;
        }
        double lat = latitude.get().doubleValue();
        double lng = longitude.get().doubleValue();
        double radius = radiusKm.get().doubleValue();
        if (radius <= 0) {
            return null;
        }
        double latDelta = radius / 111.32;
        double lngDelta = radius / (Math.cos(Math.toRadians(lat)) * 111.32);
        BigDecimal minLat = BigDecimal.valueOf(lat - latDelta);
        BigDecimal maxLat = BigDecimal.valueOf(lat + latDelta);
        BigDecimal minLng = BigDecimal.valueOf(lng - lngDelta);
        BigDecimal maxLng = BigDecimal.valueOf(lng + lngDelta);
        return (root, query, cb) -> cb.and(
            cb.between(root.get("latitude"), minLat, maxLat),
            cb.between(root.get("longitude"), minLng, maxLng)
        );
    }
}
