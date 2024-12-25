package com.user_service.services;

import com.user_service.dto.Request.LoginUserRequest;
import com.user_service.dto.Request.RegisterUserRequest;
import com.user_service.dto.Response.LoginResponse;
import com.user_service.entity.User;
import com.user_service.entity.UserRole;
import com.user_service.exceptions.FieldEmptyException;
import com.user_service.exceptions.PasswordLengthException;
import com.user_service.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User register(RegisterUserRequest registerRequest) {

        if(registerRequest.getEmail().isEmpty() || registerRequest.getName().isEmpty() || registerRequest.getPassword().isEmpty()) {
            throw new FieldEmptyException("Invalid input");
        }

        if(registerRequest.getPassword().length() < 6) {
            throw new PasswordLengthException("Invalid input");
        }

        try {
            var user = User.builder()
                    .name(registerRequest.getName())
                    .email(registerRequest.getEmail())
                    .name(registerRequest.getName())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(UserRole.USER)
                    .build();

            if(registerRequest.getPhone() != null) {
                user.setPhone(registerRequest.getPhone());
            }

            User savedUser = userRepository.save(user);

            logger.info("User registered successfully: {}", savedUser);

            return savedUser;
        }catch (Exception e) {
            logger.error("Error registering user: {}", (Object) e.getStackTrace());

            throw new RuntimeException("An error occurred while registering the user", e);
        }
    }

    public LoginResponse login(LoginUserRequest loginRequest) {

        if(loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()) {
            throw new FieldEmptyException("Invalid input");
        }

        if(loginRequest.getPassword().length() < 6) {
            throw new PasswordLengthException("Invalid input");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        User user = userRepository.findUserByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginRequest.getEmail()));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
