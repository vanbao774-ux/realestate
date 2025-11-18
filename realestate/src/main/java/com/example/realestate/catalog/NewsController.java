package com.example.realestate.catalog;

import com.example.realestate.common.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsRepository newsRepository;

    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping
    public Page<News> list(Pageable pageable) {
        return newsRepository.findByPublishedTrue(pageable);
    }

    @GetMapping("/{slug}")
    public News detail(@PathVariable String slug) {
        return newsRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("News not found"));
    }
}
