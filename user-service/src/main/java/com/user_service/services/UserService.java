package com.user_service.services;

import com.user_service.dto.Request.UserUpdateRequest;
import com.user_service.entity.User;
import com.user_service.entity.UserRole;
import com.user_service.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        // Update fields
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            if (user.getRole().equals(UserRole.USER) && request.getRole().equals("ADMIN")) {
                user.setRole(UserRole.ADMIN);
            } else if (user.getRole().equals(UserRole.ADMIN) && request.getRole().equals("USER")) {
                throw new IllegalArgumentException("Cannot change role from ADMIN to USER");
            }
        }

        userRepository.save(user);

        return user;
    }
}
