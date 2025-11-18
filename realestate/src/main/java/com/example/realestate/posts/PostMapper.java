package com.example.realestate.posts;

import com.example.realestate.posts.dto.PostResponse;
import com.example.realestate.posts.dto.PostSummary;
import java.util.Comparator;
import java.util.stream.Collectors;

import com.example.realestate.posts.PostMedia;

public final class PostMapper {

    private PostMapper() {
    }

    public static PostSummary toSummary(Post post) {
        return new PostSummary(
            post.getId(),
            post.getTitle(),
            post.getPurpose().name(),
            post.getPrice(),
            post.getArea(),
            post.getPropertyType() != null ? post.getPropertyType().getName() : null,
            post.getProvince() != null ? post.getProvince().getName() : null,
            post.getDistrict() != null ? post.getDistrict().getName() : null,
            post.getWard() != null ? post.getWard().getName() : null,
            post.getCreatedAt()
        );
    }

    public static PostResponse toResponse(Post post) {
        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getDescription(),
            post.getPurpose().name(),
            post.getStatus().name(),
            post.getPrice(),
            post.getArea(),
            post.getBedrooms(),
            post.getBathrooms(),
            post.getPropertyType() != null ? post.getPropertyType().getName() : null,
            post.getProvince() != null ? post.getProvince().getName() : null,
            post.getDistrict() != null ? post.getDistrict().getName() : null,
            post.getWard() != null ? post.getWard().getName() : null,
            post.getAddress(),
            post.getLatitude(),
            post.getLongitude(),
            post.getLegalStatus(),
            post.getOrientation() != null ? post.getOrientation().getName() : null,
            post.getFurnishingLevel() != null ? post.getFurnishingLevel().getName() : null,
            post.getViewsTotal(),
            post.getCreatedAt(),
            post.getUpdatedAt(),
            post.getExpiresAt(),
            post.getRejectReason(),
            post.getFeaturedUntil(),
            post.getAmenities().stream()
                .map(amenity -> amenity.getName())
                .collect(Collectors.toSet()),
            post.getMedia().stream()
                .sorted(Comparator.comparing(PostMedia::getSortOrder))
                .map(PostMedia::getUrl)
                .toList()
        );
    }
}
