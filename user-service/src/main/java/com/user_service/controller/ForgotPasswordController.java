package com.user_service.controller;

import com.user_service.dto.Request.ForgotPasswordRequest;
import com.user_service.dto.Request.ResetPasswordRequest;
import com.user_service.dto.Response.BaseResponse;
import com.user_service.dto.Response.ErrorResponseDto;
import com.user_service.dto.Response.ForgotPasswordResponse;
import com.user_service.services.ForgotPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password/")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {

        if (forgotPasswordRequest.getEmail() == null || forgotPasswordRequest.getEmail().isEmpty()) {
            ErrorResponseDto errorResponse = new ErrorResponseDto("Email cannot be null or empty", "Null data provided", 400, System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        forgotPasswordService.generateAndSendOtp(forgotPasswordRequest.getEmail());
        BaseResponse response = new ForgotPasswordResponse("OTP send to your register mail", "SUCCESS");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {

        if (resetPasswordRequest.getOtp() == null || resetPasswordRequest.getNewPassword() == null || resetPasswordRequest.getNewPassword().isEmpty()) {
            ErrorResponseDto errorResponse = new ErrorResponseDto("OTP and new password cannot be null or empty", "Null data provided", 400, System.currentTimeMillis());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        forgotPasswordService.resetPassword(resetPasswordRequest.getOtp(), resetPasswordRequest.getNewPassword());
        BaseResponse response = new ForgotPasswordResponse("Password reset successfully", "SUCCESS");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
