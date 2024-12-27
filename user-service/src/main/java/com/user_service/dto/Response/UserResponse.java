package com.user_service.dto.Response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.user_service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private boolean isActive;
    private String role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.isActive = user.isActive();
        this.role = user.getRole().toString();
    }
}
