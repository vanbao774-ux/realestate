package com.example.realestate.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpRequest(@NotBlank @Pattern(regexp = "^[0-9]{9,15}$") String phone) {
}
