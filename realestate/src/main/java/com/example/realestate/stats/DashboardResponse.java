package com.example.realestate.stats;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
    Map<String, Long> statusCounts,
    long totalViews,
    long totalFavorites,
    List<TopPostView> topPostsLast7Days,
    List<TopPostView> topPostsLast30Days,
    List<DailyView> viewsByDay
) {
}
