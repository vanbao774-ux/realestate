package com.example.realestate.posts;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {

    List<PostMedia> findByPostIdOrderBySortOrderAsc(Long postId);
}
