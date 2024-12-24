package com.user_service.services;

import com.user_service.entity.ForgotPassword;
import com.user_service.entity.User;
import com.user_service.exceptions.PasswordReuseException;
import com.user_service.repositories.ForgotPasswordRepository;
import com.user_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


@Service
@Slf4j
public class ForgotPasswordService {
    private static final long OTP_EXPIRATION_TIME = 10 * 60;
    private static final String FORGOT_PASSWORD = "forgot_password";
    private final AtomicReference<String> atomicEmail = new AtomicReference<>();
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;

    public ForgotPasswordService(ForgotPasswordRepository forgotPasswordRepository,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 EmailService emailService,
                                 RedisTemplate<String, Object> redisTemplate) {
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }

    public void generateAndSendOtp(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        atomicEmail.set(email);
        String key = FORGOT_PASSWORD + email;

        // Generate a 6-digit OTP
        int otp = (int) (Math.random() * 900000) + 100000;

        // OTP valid for 10 minutes
        Date expirationTime = new Date(System.currentTimeMillis() + 10 * 60 * 1000);

        Map<String, Object> existingOtpDetails = (Map<String, Object>) redisTemplate.opsForValue().get(key);

        if(existingOtpDetails != null) {
            existingOtpDetails.put("otp", otp);
            redisTemplate.opsForValue().set(key, existingOtpDetails, OTP_EXPIRATION_TIME, TimeUnit.MINUTES);
        }
        else {
            ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user).orElse(ForgotPassword.builder()
                    .user(user)
                    .build());

            forgotPassword.setOtp(otp);
            forgotPassword.setExpirationTime(expirationTime);
            forgotPasswordRepository.save(forgotPassword);

            // Save OTP details in Redis
            Map<String, Object> otpDetails = new HashMap<>();
            otpDetails.put("otp", otp);
            otpDetails.put("userId", user.getId());

            redisTemplate.opsForValue().set(key, otpDetails, OTP_EXPIRATION_TIME, TimeUnit.MINUTES);
        }

        // Send OTP to the user's email
        String subject = "Password Reset OTP";
        String body = emailService.formatEmailBody(user.getName(), String.valueOf(otp));

        emailService.sendEmail(user.getEmail(), subject, body);
        log.info("Reset password email sent to: {} at {}", user.getEmail(), LocalDateTime.now());
    }

    public void validateOtp(String email, Integer otp) {
        String key = FORGOT_PASSWORD + email;

        Map<String, Object>  otpDetails = (Map<String, Object>) redisTemplate.opsForValue().get(key);

        if (otpDetails == null) {
            throw new RuntimeException("Invalid or expired OTP!");
        }

        Integer storedOtp = (Integer) otpDetails.get("otp");

        if (storedOtp == null) {
            redisTemplate.delete(key);
            throw new RuntimeException("OTP expired!");
        }

        if (!storedOtp.equals(otp)) {
            throw new RuntimeException("Invalid OTP!");
        }
    }

    public void resetPassword(Integer otp, String newPassword) {
        String email = atomicEmail.get();

        if (email == null) {
            throw new RuntimeException("No OTP request found for any email!");
        }

        validateOtp(email, otp);

        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PasswordReuseException("You cannot reuse your previous password. Please choose a new password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove the OTP record after successful password reset
        ForgotPassword forgotPassword = forgotPasswordRepository.findByUser(user).orElseThrow(() -> new RuntimeException("No data found!"));
        forgotPasswordRepository.delete(forgotPassword);
    }
}
