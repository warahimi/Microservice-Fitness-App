package com.fitness.ai_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

//@Service
//@RequiredArgsConstructor
//public class GeminiService {
//
//
//    private final WebClient webClient = WebClient.builder().build();
//
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    @Value("${gemini.api.url}")
//    private String apiUrl;
//
//    // Method to send a prompt to Gemini API and get the response
//    /*
//        * This method constructs the request body according to Gemini API specifications,
//        {
//          "contents": [
//            {
//              "parts": [
//                {
//                  "text": "What is Spring Boot?"
//                }
//              ]
//            }
//          ]
//        }
//     */
//    public String askGemini(String prompt) {
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(
//                        Map.of("parts", List.of(
//                                Map.of("text", prompt)
//                        ))
//                )
//        );
//
//        return webClient.post()
//                .uri(apiUrl + "?key=" + apiKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }
//}

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String askGemini(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            return webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)

                    // Retry temporary 429 errors 3 times
                    .retryWhen(
                            Retry.backoff(3, Duration.ofSeconds(5))
                                    .filter(this::isTooManyRequests)
                    )

                    .block();

        } catch (WebClientResponseException.TooManyRequests ex) {
            log.error("Gemini API quota/rate limit exceeded: {}", ex.getResponseBodyAsString());

            // Return valid fallback Gemini-like JSON
            return fallbackGeminiResponse("Gemini API quota exceeded. Please try again later.");

        } catch (Exception ex) {
            log.error("Failed to call Gemini API", ex);

            return fallbackGeminiResponse("Unable to generate AI recommendation at this time.");
        }
    }

    private boolean isTooManyRequests(Throwable throwable) {
        return throwable instanceof WebClientResponseException.TooManyRequests;
    }

    private String fallbackGeminiResponse(String message) {
        return """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "{\\"analysis\\":{\\"overall\\":\\"%s\\",\\"duration\\":\\"N/A\\",\\"caloriesBurned\\":\\"N/A\\",\\"additionalMetrics\\":\\"N/A\\"},\\"improvements\\":[{\\"area\\":\\"N/A\\",\\"recommendation\\":\\"Try again later.\\"}],\\"nextWorkouts\\":[{\\"workout\\":\\"N/A\\",\\"description\\":\\"No workout suggestion available.\\"}],\\"safety\\":[\\"Follow general safety guidelines.\\"],\\"summary\\":\\"Fallback recommendation.\\"}"
                          }
                        ],
                        "role": "model"
                      }
                    }
                  ]
                }
                """.formatted(message);
    }
}