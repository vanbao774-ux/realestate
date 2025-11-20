package com.example.realestate.stats;

import java.sql.Date;
import java.time.LocalDate;

public record DailyView(LocalDate date, long totalViews) {
    public DailyView(Date date, Long totalViews) {
        this(date.toLocalDate(), totalViews);
    }
}
