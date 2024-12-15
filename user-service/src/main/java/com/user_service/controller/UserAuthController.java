package com.user_service.controller;

import com.user_service.dto.Request.RegisterUserRequest;
import com.user_service.dto.Response.UserRegisterResponse;
import com.user_service.entity.User;
import com.user_service.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/auth")
public class UserAuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Object> userRegister(@Valid @RequestBody RegisterUserRequest registerUserRequest) {

        System.out.println("Phone here: "+ registerUserRequest.getPhone());

        User u = authService.register(registerUserRequest);
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse("User saved successfully!","SUCCESS", u);

        return new ResponseEntity<>(userRegisterResponse, HttpStatus.CREATED);
    }

}
