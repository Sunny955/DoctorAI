package com.user_service.controller;

import com.user_service.dto.Request.LoginUserRequest;
import com.user_service.dto.Request.RefreshTokenRequest;
import com.user_service.dto.Request.RegisterUserRequest;
import com.user_service.dto.Response.BaseResponse;
import com.user_service.dto.Response.ForgotPasswordResponse;
import com.user_service.dto.Response.LoginResponse;
import com.user_service.dto.Response.UserRegisterResponse;
import com.user_service.entity.RefreshToken;
import com.user_service.entity.User;
import com.user_service.services.AuthService;
import com.user_service.services.ForgotPasswordService;
import com.user_service.services.JwtService;
import com.user_service.services.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/user/auth")
public class UserAuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final ForgotPasswordService forgotPasswordService;

    public UserAuthController(AuthService authService,
                              RefreshTokenService refreshTokenService,
                              JwtService jwtService,
                              ForgotPasswordService forgotPasswordService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> userRegister(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        User u = authService.register(registerUserRequest);
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse("User saved successfully!","SUCCESS", u);
        return new ResponseEntity<>(userRegisterResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> userLogin(@Valid @RequestBody LoginUserRequest loginUserRequest) {
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

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestParam String email) {
        forgotPasswordService.generateAndSendOtp(email);
        BaseResponse response = new ForgotPasswordResponse("OTP send to your register mail", "SUCCESS");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestParam Integer otp, @RequestParam String newPassword) {
        forgotPasswordService.resetPassword(otp, newPassword);
        BaseResponse response = new ForgotPasswordResponse("Password reset successfully", "SUCCESS");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
