package com.fitness.activity_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUserId(String userId) {
        try {
            return userServiceWebClient.get()
                    .uri("/api/users/validate/{userId}", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block(); // User exists
        }
        catch (WebClientResponseException e)
        {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false; // User does not exist
            }
        }
        catch (Exception e) {
            return false; // User does not exist or an error occurred
        }
        return false; // Default to false if an error occurs
    }
}
