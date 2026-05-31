package com.fitness.activity_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fitness.activity_service.model.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    @NotNull(message = "Activity type is required")
    private ActivityType type;
    @NotBlank(message = "Activity name is required")
    @Size(min = 1, message = "Activity name must be at least 1")
    private Integer duration;
    @NotBlank(message = "Colonies Burned is required")
    private Integer caloriesBurned;
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    @NotBlank(message = "Start time is required")
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
}
