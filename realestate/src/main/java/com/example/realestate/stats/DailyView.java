package com.example.realestate.stats;

import java.time.LocalDate;

public record DailyView(LocalDate date, long totalViews) {
}
