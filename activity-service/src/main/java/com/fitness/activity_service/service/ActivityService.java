package com.fitness.activity_service.service;

import com.fitness.activity_service.ActivityRepository;
import com.fitness.activity_service.dto.ActivityRequest;
import com.fitness.activity_service.dto.ActivityResponse;
import com.fitness.activity_service.exceptions.ActivityNotFoundException;
import com.fitness.activity_service.model.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    public ActivityResponse createActivity(ActivityRequest request) {
        Activity activity = maptoActivity(request);
        Activity savedActivity = activityRepository.save(activity);
        return mapToActivityResponse(savedActivity);
    }
    private Activity maptoActivity(ActivityRequest request) {
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
}
