package com.fitness.ai_service.service;

import com.fitness.ai_service.dto.ActivityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageListener {
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processActivity(ActivityResponse activityResponse)
    {
        log.info("Received activity message: {}", activityResponse);
        // Here you can call the recommendation service to generate recommendations based on the activity
    }
}
