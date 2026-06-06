package com.fitness.ai_service.service;

import com.fitness.ai_service.dto.RecommendationResponse;
import com.fitness.ai_service.model.Recommendation;
import com.fitness.ai_service.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;

    public List<RecommendationResponse> getRecommendationsForUser(String userId) {
        List<Recommendation> recommendationsByUserId = recommendationRepository.findByUserId(userId);
        return recommendationsByUserId.stream()
                .map(this::mapToRecoomendationResponse)
                .toList();
    }
    
    private RecommendationResponse mapToRecoomendationResponse(Recommendation recommendation) {
        return RecommendationResponse.builder()
                .id(recommendation.getId())
                .activityId(recommendation.getActivityId())
                .userId(recommendation.getUserId())
                .activityType(recommendation.getActivityType())
                .recommendation(recommendation.getRecommendation())
                .improvements(recommendation.getImprovements())
                .suggestions(recommendation.getSuggestions())
                .safetyMeasures(recommendation.getSafetyMeasures())
                .createdAt(recommendation.getCreatedAt())
                .build();
    }

    public RecommendationResponse getRecommendationsForActivity(String activityId) {
        Optional<Recommendation> byActivityId = recommendationRepository.findByActivityId(activityId);
        if(byActivityId.isPresent()) {
            return mapToRecoomendationResponse(byActivityId.get());
        } else {
            throw new RuntimeException("No recommendation found for activityId: " + activityId);
        }
    }

    public List<RecommendationResponse> getAllRecommendations() {
        List<Recommendation> allRecommendations = recommendationRepository.findAll();
        return allRecommendations.stream()
                .map(this::mapToRecoomendationResponse)
                .toList();
    }
}
