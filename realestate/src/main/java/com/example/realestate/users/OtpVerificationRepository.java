package com.example.realestate.users;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("delete from OtpVerification o where o.expiresAt < :threshold")
    int deleteExpired(@Param("threshold") Instant threshold);
}
