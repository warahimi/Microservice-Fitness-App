package com.fitness.activity_service;

import com.fitness.activity_service.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, String> {
    List<Activity> findByUserId(String userId);
}
