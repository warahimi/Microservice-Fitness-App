package com.fitness.ai_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationResponse {
    private String id;
    private String activityId; // to which activity this recommendation belongs
    private String userId; // for which user this recommendation is generated
    private String activityType; // e.g., "Running", "Cycling"
    private String recommendation;
    private List<String> improvements;
    private List<String> suggestions;
    private List<String> safetyMeasures;
    LocalDateTime createdAt;
}
