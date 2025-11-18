package com.example.realestate.auth;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/phone/otp")
    public void requestOtp(@Valid @RequestBody OtpRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        authService.requestOtp(principal.getUser().getId(), request.phone());
    }

    @PostMapping("/phone/verify")
    public void verifyOtp(@Valid @RequestBody OtpVerifyRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        authService.verifyOtp(principal.getUser().getId(), request.phone(), request.otp());
    }
}
