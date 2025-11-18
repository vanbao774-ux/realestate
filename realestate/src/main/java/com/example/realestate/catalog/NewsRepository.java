package com.example.realestate.catalog;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
    Optional<News> findBySlug(String slug);

    Page<News> findByPublishedTrue(Pageable pageable);
}
