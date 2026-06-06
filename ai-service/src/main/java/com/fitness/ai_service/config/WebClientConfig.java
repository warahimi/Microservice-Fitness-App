package com.fitness.ai_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Creates a WebClient bean so Spring can inject it into GeminiService
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
