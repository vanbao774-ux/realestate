package com.example.realestate.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public NotificationService(JavaMailSender mailSender, @Value("${spring.mail.username:}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromAddress != null && !fromAddress.isBlank()) {
                message.setFrom(fromAddress);
            }
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }

    public void sendOtpSms(String phone, String otp) {
        log.info("Sending OTP {} to phone {}", otp, phone);
    }
}
