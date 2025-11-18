package com.example.realestate.posts.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectPostRequest(@NotBlank String reason) {
}
