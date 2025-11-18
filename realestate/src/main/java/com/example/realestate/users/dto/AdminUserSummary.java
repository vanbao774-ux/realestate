package com.example.realestate.users.dto;

import java.time.Instant;
import java.util.Set;

public record AdminUserSummary(
    Long id,
    String email,
    String fullName,
    String phone,
    boolean active,
    Instant createdAt,
    Instant phoneVerifiedAt,
    Set<String> roles
) {
}
