package com.fitness.app.user_service.dto;

import com.fitness.app.user_service.model.UserRole;
import jakarta.validation.constraints.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 16, message = "First name must be between 3 and 16 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 16, message = "Last name must be between 3 and 16 characters")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must contain a valid domain"
    )
    private String email;

    @NotNull(message = "User role is required and must be either USER or ADMIN")
    private UserRole userRole;
}