package com.user_service.services;

import com.user_service.dto.Request.RegisterUserRequest;
import com.user_service.entity.User;
import com.user_service.entity.UserRole;
import com.user_service.exceptions.InvalidRequestException;
import com.user_service.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User register(RegisterUserRequest registerRequest) {
        try {
            var user = User.builder()
                    .name(registerRequest.getName())
                    .email(registerRequest.getEmail())
                    .name(registerRequest.getName())
                    .phone(registerRequest.getPhone())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(UserRole.USER)
                    .build();

            User savedUser = userRepository.save(user);

            logger.info("User registered successfully: {}", savedUser);

            return savedUser;
        }catch (Exception e) {
            logger.error("Error registering user: {}", (Object) e.getStackTrace());

            throw new RuntimeException("An error occurred while registering the user", e);
        }
    }
}
