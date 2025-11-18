package com.example.realestate.auth;

import com.example.realestate.admin.AuditService;
import com.example.realestate.common.ResourceNotFoundException;
import com.example.realestate.users.Role;
import com.example.realestate.users.RoleRepository;
import com.example.realestate.users.User;
import com.example.realestate.users.UserRepository;
import com.example.realestate.auth.UserPrincipal;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpService otpService;
    private final AuditService auditService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        RefreshTokenRepository refreshTokenRepository,
        OtpService otpService,
        AuditService auditService,
        AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.otpService = otpService;
        this.auditService = auditService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new IllegalArgumentException("Email already registered");
        });
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseGet(() -> roleRepository.save(newRole("ROLE_USER")));
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        user.setActive(true);
        user.setRoles(Set.of(userRole));
        User saved = userRepository.save(user);
        auditService.record(saved, "REGISTER", "USER", saved.getId(), Map.of("email", saved.getEmail()));
        return issueTokens(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = ((UserPrincipal) authentication.getPrincipal()).getUser();
        if (!user.isActive()) {
            throw new IllegalStateException("Account locked");
        }
        auditService.record(user, "LOGIN", "USER", user.getId(), Map.of());
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
            .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return issueTokens(refreshToken.getUser());
    }

    public void requestOtp(Long userId, String phone) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        otpService.requestOtp(user, phone);
    }

    public void verifyOtp(Long userId, String phone, String otp) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        otpService.verifyOtp(user, phone, otp);
        auditService.record(user, "VERIFY_PHONE", "USER", user.getId(), Map.of("phone", phone));
    }

    private AuthResponse issueTokens(User user) {
        refreshTokenRepository.revokeExpiredByUser(user.getId(), Instant.now());
        String accessToken = jwtService.generateAccessToken(
            user.getEmail(),
            Map.of(
                "userId", user.getId(),
                "roles", user.getRoles().stream().map(Role::getName).toList()
            )
        );
        RefreshToken refresh = new RefreshToken();
        refresh.setUser(user);
        refresh.setToken(jwtService.generateRefreshToken(user.getEmail() + ":" + Instant.now().toEpochMilli()));
        refresh.setExpiresAt(Instant.now().plus(jwtService.getRefreshTokenTtlSeconds(), ChronoUnit.SECONDS));
        refreshTokenRepository.save(refresh);
        return AuthResponse.bearer(accessToken, refresh.getToken(), jwtService.getAccessTokenTtlSeconds());
    }

    private Role newRole(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }
}
