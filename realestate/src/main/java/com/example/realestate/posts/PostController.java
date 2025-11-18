package com.example.realestate.posts;

import com.example.realestate.posts.dto.PostFilter;
import com.example.realestate.posts.dto.PostRequest;
import com.example.realestate.posts.dto.PostResponse;
import com.example.realestate.posts.dto.PostSummary;
import com.example.realestate.posts.dto.StatusUpdateRequest;
import com.example.realestate.auth.UserPrincipal;
import com.example.realestate.posts.PostStatus;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
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
        @RequestParam Optional<BigDecimal> latitude,
        @RequestParam Optional<BigDecimal> longitude,
        @RequestParam Optional<BigDecimal> radiusKm,
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
            status.or(() -> Optional.of("APPROVED")),
            latitude,
            longitude,
            radiusKm
        );
        return postService.search(filter, pageable);
    }

    @GetMapping("/{id}")
    public PostResponse getPost(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal principal,
        HttpServletRequest request
    ) {
        return postService.getPost(id, principal != null ? principal.getUser() : null, request.getRemoteAddr());
    }

    @PostMapping
    public PostResponse create(@Valid @RequestBody PostRequest request) {
        return postService.create(request);
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        return postService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        postService.changeStatus(id, PostStatus.valueOf(request.status().toUpperCase()));
    }
}
