package com.user_service.controller;

import com.user_service.dto.Request.UserUpdateRequest;
import com.user_service.dto.Response.BaseResponse;
import com.user_service.dto.Response.UserResponse;
import com.user_service.dto.Response.UserUpdateResponse;
import com.user_service.entity.User;
import com.user_service.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getUsersList(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);

        Page<UserResponse> userResponses = users.map(user -> new UserResponse(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.isActive(),
                user.getRole().name()
        ));

        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @PutMapping("/users/update/{id}")
    public ResponseEntity<Object> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        User updatedUser = userService.updateUser(id, request);
        UserResponse userResponse = new UserResponse(updatedUser);
        BaseResponse response = new UserUpdateResponse("200", "User updated successfully", userResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}