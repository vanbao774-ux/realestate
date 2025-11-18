package com.example.realestate.admin;

import com.example.realestate.catalog.Banner;
import com.example.realestate.catalog.BannerRepository;
import com.example.realestate.catalog.dto.BannerRequest;
import com.example.realestate.common.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/banners")
public class AdminBannerController {

    private final BannerRepository bannerRepository;

    public AdminBannerController(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @GetMapping
    public Page<Banner> list(Pageable pageable) {
        return bannerRepository.findAll(pageable);
    }

    @PostMapping
    public Banner create(@Valid @RequestBody BannerRequest request) {
        Banner banner = new Banner();
        apply(banner, request);
        return bannerRepository.save(banner);
    }

    @PutMapping("/{id}")
    public Banner update(@PathVariable Long id, @Valid @RequestBody BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));
        apply(banner, request);
        return bannerRepository.save(banner);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bannerRepository.deleteById(id);
    }

    private void apply(Banner banner, BannerRequest request) {
        banner.setPosition(request.position());
        banner.setImageUrl(request.imageUrl());
        banner.setTargetUrl(request.targetUrl());
        banner.setActive(request.active());
        banner.setSortOrder(request.sortOrder());
    }
}
