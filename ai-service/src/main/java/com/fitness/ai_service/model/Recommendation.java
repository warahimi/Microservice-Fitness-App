package com.fitness.ai_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "recommendations")
public class Recommendation {
    @Id
    private String id;
    private String activityId; // to which activity this recommendation belongs
    private String userId; // for which user this recommendation is generated
    private String activityType; // e.g., "Running", "Cycling"
    private String recommendation;
    private List<String> improvements;
    private List<String> suggestions;
    private List<String> safetyMeasures;
    @CreatedDate
    LocalDateTime createdAt;

}
