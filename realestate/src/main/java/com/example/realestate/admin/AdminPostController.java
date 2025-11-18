package com.example.realestate.admin;

import com.example.realestate.posts.PostService;
import com.example.realestate.posts.PostStatus;
import com.example.realestate.posts.dto.ApprovePostRequest;
import com.example.realestate.posts.dto.PostFilter;
import com.example.realestate.posts.dto.PostSummary;
import com.example.realestate.posts.dto.RejectPostRequest;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/posts")
@Validated
public class AdminPostController {

    private final PostService postService;

    public AdminPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Page<PostSummary> search(
        @RequestParam Optional<String> q,
        @RequestParam Optional<String> purpose,
        @RequestParam Optional<Long> propertyTypeId,
        @RequestParam Optional<Long> provinceId,
        @RequestParam Optional<Long> districtId,
        @RequestParam Optional<Long> wardId,
        @RequestParam Optional<BigDecimal> priceMin,
        @RequestParam Optional<BigDecimal> priceMax,
        @RequestParam Optional<BigDecimal> areaMin,
        @RequestParam Optional<BigDecimal> areaMax,
        @RequestParam Optional<Integer> bedrooms,
        @RequestParam Optional<Integer> bathrooms,
        @RequestParam Optional<Long> orientationId,
        @RequestParam Optional<Long> furnishingLevelId,
        @RequestParam Optional<String> status,
        Pageable pageable
    ) {
        PostFilter filter = new PostFilter(
            q,
            purpose,
            propertyTypeId,
            provinceId,
            districtId,
            wardId,
            priceMin,
            priceMax,
            areaMin,
            areaMax,
            bedrooms,
            bathrooms,
            orientationId,
            furnishingLevelId,
            status,
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
        return postService.search(filter, pageable);
    }

    @PatchMapping("/{id}/approve")
    public void approve(@PathVariable Long id, @RequestBody(required = false) ApprovePostRequest request) {
        java.time.Instant expiresAt = request != null ? request.expiresAt() : null;
        java.time.Instant featuredUntil = request != null ? request.featuredUntil() : null;
        postService.approvePost(id, expiresAt, featuredUntil);
    }

    @PatchMapping("/{id}/reject")
    public void reject(@PathVariable Long id, @RequestBody @Validated RejectPostRequest request) {
        postService.rejectPost(id, request.reason());
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(@PathVariable Long id, @RequestBody @Validated com.example.realestate.posts.dto.StatusUpdateRequest request) {
        postService.changeStatus(id, PostStatus.valueOf(request.status().toUpperCase()));
    }
}
