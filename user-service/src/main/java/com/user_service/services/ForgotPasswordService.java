package com.user_service.services;

import com.user_service.entity.ForgotPassword;
import com.user_service.entity.User;
import com.user_service.repositories.ForgotPasswordRepository;
import com.user_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;


@Service
@Slf4j
public class ForgotPasswordService {
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ForgotPasswordService(ForgotPasswordRepository forgotPasswordRepository,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 EmailService emailService) {
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void generateAndSendOtp(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Generate a 6-digit OTP
        int otp = (int) (Math.random() * 900000) + 100000;

        // OTP valid for 10 minutes
        Date expirationTime = new Date(System.currentTimeMillis() + 10 * 60 * 1000);

        ForgotPassword existingForgotPassword = forgotPasswordRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Data not found"));

        if (existingForgotPassword !=null) {
            existingForgotPassword.setOtp(otp);
            existingForgotPassword.setExpirationTime(expirationTime);
            forgotPasswordRepository.save(existingForgotPassword);
        } else {
            ForgotPassword forgotPassword = ForgotPassword.builder()
                    .otp(otp)
                    .expirationTime(expirationTime)
                    .user(user)
                    .build();
            forgotPasswordRepository.save(forgotPassword);
        }

        // Send OTP to the user's email
        String subject = "Password Reset OTP";
        String body = emailService.formatEmailBody(user.getName(), String.valueOf(otp));

        emailService.sendEmail(user.getEmail(), subject, body);
        log.info("Reset password email sent to: {} at {}", user.getEmail(), LocalDateTime.now());
    }

    public void validateOtp(Integer otp) {
        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtp(otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP!"));

        if (forgotPassword.getExpirationTime().before(new Date())) {
            forgotPasswordRepository.delete(forgotPassword);
            throw new RuntimeException("OTP expired!");
        }
    }

    public void resetPassword(Integer otp, String newPassword) {
        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtp(otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP!"));

        validateOtp(otp);

        User user = forgotPassword.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove the OTP record after successful password reset
        forgotPasswordRepository.delete(forgotPassword);
    }
}
