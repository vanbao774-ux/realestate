package com.example.realestate.admin;

import com.example.realestate.catalog.News;
import com.example.realestate.catalog.NewsRepository;
import com.example.realestate.catalog.dto.NewsRequest;
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
@RequestMapping("/api/admin/news")
public class AdminNewsController {

    private final NewsRepository newsRepository;

    public AdminNewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping
    public Page<News> list(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    @PostMapping
    public News create(@Valid @RequestBody NewsRequest request) {
        News news = new News();
        apply(news, request);
        return newsRepository.save(news);
    }

    @PutMapping("/{id}")
    public News update(@PathVariable Long id, @Valid @RequestBody NewsRequest request) {
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("News not found"));
        apply(news, request);
        return newsRepository.save(news);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        newsRepository.deleteById(id);
    }

    private void apply(News news, NewsRequest request) {
        news.setTitle(request.title());
        news.setSlug(request.slug());
        news.setSummary(request.summary());
        news.setContent(request.content());
        news.setCoverUrl(request.coverUrl());
        news.setPublished(request.published());
        news.setPublishedAt(request.publishedAt());
    }
}
