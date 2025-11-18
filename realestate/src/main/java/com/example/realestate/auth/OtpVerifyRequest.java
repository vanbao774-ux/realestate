package com.example.realestate.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpVerifyRequest(
    @NotBlank @Pattern(regexp = "^[0-9]{9,15}$") String phone,
    @NotBlank @Pattern(regexp = "^[0-9]{6}$") String otp
) {
}
