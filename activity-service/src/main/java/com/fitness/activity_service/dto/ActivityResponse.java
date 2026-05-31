package com.fitness.activity_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fitness.activity_service.model.ActivityType;

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
public class ActivityResponse {
    private String id;
    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
}
