package com.user_service.controller;

import com.user_service.dto.Request.LoginUserRequest;
import com.user_service.dto.Request.RefreshTokenRequest;
import com.user_service.dto.Request.RegisterUserRequest;
import com.user_service.dto.Response.LoginResponse;
import com.user_service.dto.Response.UserRegisterResponse;
import com.user_service.dto.Response.UserResponse;
import com.user_service.entity.RefreshToken;
import com.user_service.entity.User;
import com.user_service.services.AuthService;
import com.user_service.services.ForgotPasswordService;
import com.user_service.services.JwtService;
import com.user_service.services.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/user/auth")
public class UserAuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public UserAuthController(AuthService authService,
                              RefreshTokenService refreshTokenService,
                              JwtService jwtService,
                              ForgotPasswordService forgotPasswordService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> userRegister(@RequestBody RegisterUserRequest registerUserRequest) {
        User u = authService.register(registerUserRequest);
        UserResponse response = new UserResponse(u);
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse("User saved successfully!","SUCCESS", response);
        return new ResponseEntity<>(userRegisterResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> userLogin(@RequestBody LoginUserRequest loginUserRequest) {
        return new ResponseEntity<>(authService.login(loginUserRequest), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user);
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<Object, Object> response = authService.validate(authHeader);
        String token = (String) response.get("token");

        if(token.startsWith("Invalid")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
