package com.example.realestate.posts.dto;

import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull String status) {
}
