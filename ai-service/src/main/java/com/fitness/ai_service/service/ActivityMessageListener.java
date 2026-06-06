package com.fitness.ai_service.service;

import com.fitness.ai_service.dto.Activity;
import com.fitness.ai_service.dto.RecommendationResponse;
import com.fitness.ai_service.model.Recommendation;
import com.fitness.ai_service.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;
    private final RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processActivity(Activity activity) {
        log.info("Received activity message: {}", activity);
        log.info("Generating recommendation for activity: {}", activity.getId());

        Recommendation recommendation = activityAIService.generateRecommendation(activity);

        Recommendation savedRecommendation = recommendationRepository.save(recommendation);

        log.info("Saved recommendation with id: {}", savedRecommendation.getId());
        log.info("Saved Recommendation:{} \n", savedRecommendation);
    }
}