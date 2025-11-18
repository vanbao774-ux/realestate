package com.example.realestate.stats;

import com.example.realestate.posts.FavoriteService;
import com.example.realestate.posts.PostRepository;
import com.example.realestate.posts.PostStatus;
import com.example.realestate.posts.PostViewRepository;
import com.example.realestate.users.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final PostRepository postRepository;
    private final FavoriteService favoriteService;
    private final PostViewRepository postViewRepository;

    public DashboardService(
        PostRepository postRepository,
        FavoriteService favoriteService,
        PostViewRepository postViewRepository
    ) {
        this.postRepository = postRepository;
        this.favoriteService = favoriteService;
        this.postViewRepository = postViewRepository;
    }

    public DashboardResponse buildDashboard(User user) {
        Map<String, Long> statusCounts = new java.util.LinkedHashMap<>();
        for (PostStatus status : PostStatus.values()) {
            statusCounts.put(status.name(), postRepository.countByOwnerIdAndStatus(user.getId(), status));
        }
        long totalViews = postRepository.sumViewsByOwner(user.getId());
        long totalFavorites = favoriteService.countFavoritesForOwner(user.getId());
        Instant now = Instant.now();
        List<TopPostView> top7 = postViewRepository.findTopPosts(user.getId(), now.minus(7, ChronoUnit.DAYS), PageRequest.of(0, 5));
        List<TopPostView> top30 = postViewRepository.findTopPosts(user.getId(), now.minus(30, ChronoUnit.DAYS), PageRequest.of(0, 5));
        List<DailyView> dailyViews = postViewRepository.aggregateViews(user.getId(), now.minus(30, ChronoUnit.DAYS));
        return new DashboardResponse(statusCounts, totalViews, totalFavorites, top7, top30, dailyViews);
    }
}
