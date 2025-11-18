package com.example.realestate.auth;

import com.example.realestate.common.NotificationService;
import com.example.realestate.users.OtpVerification;
import com.example.realestate.users.OtpVerificationRepository;
import com.example.realestate.users.User;
import com.example.realestate.users.UserRepository;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final OtpVerificationRepository otpRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final long expiryMinutes;

    public OtpService(
        OtpVerificationRepository otpRepository,
        UserRepository userRepository,
        NotificationService notificationService,
        @Value("${app.otp.expiry-minutes:10}") long expiryMinutes
    ) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.expiryMinutes = expiryMinutes;
    }

    @Transactional
    public void requestOtp(User user, String phone) {
        userRepository.findByPhone(phone)
            .filter(existing -> !existing.getId().equals(user.getId()))
            .ifPresent(existing -> {
                throw new IllegalArgumentException("Phone number already verified by another account");
            });
        otpRepository.deleteExpired(Instant.now());
        OtpVerification verification = new OtpVerification();
        verification.setUser(user);
        verification.setPhone(phone);
        verification.setOtpCode(String.format("%06d", RANDOM.nextInt(1_000_000)));
        verification.setExpiresAt(Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES));
        otpRepository.save(verification);
        notificationService.sendOtpSms(phone, verification.getOtpCode());
    }

    @Transactional
    public void verifyOtp(User user, String phone, String otp) {
        OtpVerification verification = otpRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("No OTP request found"));
        if (!verification.getPhone().equals(phone)) {
            throw new IllegalArgumentException("Phone mismatch");
        }
        if (verification.getVerifiedAt() != null) {
            return;
        }
        if (verification.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("OTP expired");
        }
        if (!verification.getOtpCode().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }
        verification.setVerifiedAt(Instant.now());
        user.setPhone(phone);
        user.setPhoneVerifiedAt(Instant.now());
        userRepository.save(user);
    }
}
