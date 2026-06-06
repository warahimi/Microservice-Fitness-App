package com.fitness.activity_service.service;

import com.fitness.activity_service.repository.ActivityRepository;
import com.fitness.activity_service.dto.ActivityRequest;
import com.fitness.activity_service.dto.ActivityResponse;
import com.fitness.activity_service.exceptions.ActivityNotFoundException;
import com.fitness.activity_service.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse createActivity(ActivityRequest request) {

        Activity activity = maptoActivity(request);
        Activity savedActivity = activityRepository.save(activity);

        ActivityResponse response = mapToActivityResponse(savedActivity);

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, response);

            log.info("Activity published to RabbitMQ. activityId: {}", savedActivity.getId());

        } catch (Exception ex) {
            log.error(
                    "Failed to publish activity to RabbitMQ. activityId: {}, error: {}",
                    savedActivity.getId(),
                    ex.getMessage()
            );
        }

        return response;
    }
    public List<ActivityResponse> getActivityByUserId(String userId) {
        List<Activity> activities = activityRepository.findByUserId(userId);
        return activities.stream()
                .map(this::mapToActivityResponse)
                .toList();
    }

    public ActivityResponse getActivityById(String activityId) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        if(activity.isEmpty()) {
            throw new ActivityNotFoundException("Activity not found with id: " + activityId);
        }
        return mapToActivityResponse(activity.get());
    }

    /*
        Utility function
        - Maps ActivityRequest to Activity entity
        - Validates the userId using UserValidationService before mapping
     */
    private Activity maptoActivity(ActivityRequest request) {
        // Validate the use by id
        if(!userValidationService.validateUserId(request.getUserId())) {
            throw new IllegalArgumentException("Invalid user id: " + request.getUserId());
        }
        return Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();
    }
    private ActivityResponse mapToActivityResponse(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType())
                .duration(activity.getDuration())
                .caloriesBurned(activity.getCaloriesBurned())
                .startTime(activity.getStartTime())
                .additionalMetrics(activity.getAdditionalMetrics())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
    }
}
