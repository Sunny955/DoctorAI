package com.user_service.controller;

import com.user_service.dto.Request.LoginUserRequest;
import com.user_service.dto.Request.RegisterUserRequest;
import com.user_service.dto.Response.UserRegisterResponse;
import com.user_service.entity.User;
import com.user_service.services.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/user/auth")
public class UserAuthController {
    private final AuthService authService;

    public UserAuthController(AuthService authService) {
        this.authService = authService;
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
}
