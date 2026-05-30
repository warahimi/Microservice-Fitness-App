package com.fitness.app.user_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fitness.app.user_service.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private UserRole userRole;
    @JsonFormat(pattern = "MM/dd/yyyy hh:mm a")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "MM/dd/yyyy hh:mm a")
    private LocalDateTime updatedAt;
}
