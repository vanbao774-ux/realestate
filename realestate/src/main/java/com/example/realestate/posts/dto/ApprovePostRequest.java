package com.example.realestate.posts.dto;

import java.time.Instant;

public record ApprovePostRequest(Instant expiresAt, Instant featuredUntil) {
}
