package com.fitness.activity_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    @Enumerated(EnumType.STRING)
    private ActivityType type; // Enum for activity type (e.g., RUNNING, CYCLING, SWIMMING)
    private Integer duration; // Duration in minutes
    private Integer caloriesBurned;
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    private LocalDateTime startTime;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> additionalMetrics;
    @CreationTimestamp
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
