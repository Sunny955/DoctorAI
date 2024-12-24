package com.user_service.services;

import com.user_service.entity.ForgotPassword;
import com.user_service.entity.User;
import com.user_service.exceptions.OtpExpiredException;
import com.user_service.exceptions.PasswordReuseException;
import com.user_service.repositories.ForgotPasswordRepository;
import com.user_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;


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

        ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user).orElse(ForgotPassword.builder()
                .user(user)
                .build());

        forgotPassword.setOtp(otp);
        forgotPassword.setExpirationTime(expirationTime);

        forgotPasswordRepository.save(forgotPassword);

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
            throw new OtpExpiredException("OTP expired!");
        }
    }

    public void resetPassword(Integer otp, String newPassword) {
        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtp(otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP!"));

        validateOtp(otp);

        User user = forgotPassword.getUser();

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PasswordReuseException("You cannot reuse your previous password. Please choose a new password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove the OTP record after successful password reset
        forgotPasswordRepository.delete(forgotPassword);
    }
}
