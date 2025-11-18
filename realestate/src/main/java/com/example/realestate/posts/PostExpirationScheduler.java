package com.example.realestate.posts;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class PostExpirationScheduler {

    private final PostRepository postRepository;

    public PostExpirationScheduler(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void expirePosts() {
        int updated = postRepository.expirePosts(Instant.now());
        if (updated > 0) {
            log.info("Expired {} posts", updated);
        }
    }
}
