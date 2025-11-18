package com.example.realestate.posts;

import com.example.realestate.users.User;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class PostViewService {

    private final PostViewRepository postViewRepository;

    public PostViewService(PostViewRepository postViewRepository) {
        this.postViewRepository = postViewRepository;
    }

    @Transactional
    public void registerView(Post post, User viewer, String ipAddress) {
        Instant now = Instant.now();
        Long viewerId = viewer != null ? viewer.getId() : null;
        postViewRepository.findDailyView(post.getId(), viewerId, ipAddress, now)
            .ifPresentOrElse(
                existing -> {},
                () -> {
                    PostView view = new PostView();
                    view.setPost(post);
                    view.setViewer(viewer);
                    view.setViewerIp(ipAddress);
                    view.setViewedAt(now);
                    postViewRepository.save(view);
                    post.setViewsTotal(post.getViewsTotal() + 1);
                }
            );
    }
}
