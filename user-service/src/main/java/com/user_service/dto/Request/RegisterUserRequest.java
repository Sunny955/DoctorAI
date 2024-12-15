package com.user_service.dto.Request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {
    @Size(min = 1, message = "Name cannot be null or empty")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    @Size(min = 6, message = "Password cannot be null or empty")
    private String password;
}
