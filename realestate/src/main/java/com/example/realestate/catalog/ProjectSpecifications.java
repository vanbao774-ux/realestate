package com.example.realestate.catalog;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.realestate.catalog.ProjectStatus;

public final class ProjectSpecifications {

    private ProjectSpecifications() {
    }

    public static Specification<Project> filter(
        Optional<Long> provinceId,
        Optional<Long> districtId,
        Optional<Long> wardId,
        Optional<String> status,
        Optional<BigDecimal> priceFrom,
        Optional<BigDecimal> priceTo,
        Optional<String> keyword
    ) {
        return Specification.where(equal("province", provinceId))
            .and(equal("district", districtId))
            .and(equal("ward", wardId))
            .and(status(status))
            .and(priceRange(priceFrom, priceTo))
            .and(keyword(keyword));
    }

    private static Specification<Project> equal(String association, Optional<Long> id) {
        return id.map(value -> (Specification<Project>) (root, query, cb) ->
            cb.equal(root.join(association).get("id"), value))
            .orElse(null);
    }

    private static Specification<Project> status(Optional<String> status) {
        return status.filter(StringUtils::hasText)
            .map(value -> (Specification<Project>) (root, query, cb) ->
                cb.equal(root.get("status"), ProjectStatus.valueOf(value.toUpperCase())))
            .orElse(null);
    }

    private static Specification<Project> priceRange(Optional<BigDecimal> from, Optional<BigDecimal> to) {
        Specification<Project> spec = null;
        if (from.isPresent()) {
            BigDecimal min = from.get();
            spec = (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("priceFrom"), min);
        }
        if (to.isPresent()) {
            BigDecimal max = to.get();
            Specification<Project> maxSpec = (root, query, cb) -> cb.lessThanOrEqualTo(root.get("priceTo"), max);
            spec = spec == null ? maxSpec : spec.and(maxSpec);
        }
        return spec;
    }

    private static Specification<Project> keyword(Optional<String> keyword) {
        return keyword.filter(StringUtils::hasText)
            .map(value -> (Specification<Project>) (root, query, cb) -> {
                String pattern = "%" + value.toLowerCase() + "%";
                return cb.like(cb.lower(root.get("name")), pattern);
            })
            .orElse(null);
    }
}
