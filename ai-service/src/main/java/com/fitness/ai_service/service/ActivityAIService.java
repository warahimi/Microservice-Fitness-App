package com.fitness.ai_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.ai_service.dto.Activity;
import com.fitness.ai_service.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAIService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);

        String aiResponse = geminiService.askGemini(prompt);

        return processAIResponse(activity, aiResponse);
    }

    private Recommendation processAIResponse(Activity activity, String aiResponse) {
        try {
            // Parse full Gemini API response
            JsonNode rootNode = objectMapper.readTree(aiResponse);

            // Extract Gemini text response:
            // candidates[0].content.parts[0].text
            JsonNode textNode = rootNode
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                log.warn("Gemini response does not contain text. Full response: {}", aiResponse);
                return buildDefaultRecommendation(activity);
            }

            // Gemini sometimes returns JSON inside markdown blocks.
            // This removes ```json and ``` if they exist.
            String jsonContent = cleanJsonResponse(textNode.asText());

            log.info("Extracted AI JSON content: {}", jsonContent);

            // Parse the JSON produced by Gemini
            JsonNode recommendationJson = objectMapper.readTree(jsonContent);

            // Extract analysis section
            JsonNode analysisNode = recommendationJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();

            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall Performance");
            addAnalysisSection(fullAnalysis, analysisNode, "duration", "Duration Analysis");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned Analysis");
            addAnalysisSection(fullAnalysis, analysisNode, "additionalMetrics", "Additional Metrics Analysis");

            // Extract list sections
            List<String> improvements = extractImprovements(recommendationJson.path("improvements"));
            List<String> nextWorkouts = extractNextWorkouts(recommendationJson.path("nextWorkouts"));
            List<String> safety = extractSafetyGuidelines(recommendationJson.path("safety"));

            // Build Recommendation document to save in MongoDB
            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType() != null ? activity.getType().name() : "UNKNOWN")
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(nextWorkouts)
                    .safetyMeasures(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception ex) {
            log.error("Failed to process Gemini AI response. Response: {}", aiResponse, ex);
            return buildDefaultRecommendation(activity);
        }
    }

    private String cleanJsonResponse(String responseText) {
        return responseText
                .replaceAll("(?i)```json", "")
                .replaceAll("```", "")
                .trim();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safetyList = new ArrayList<>();

        if (safetyNode.isArray()) {
            for (JsonNode safety : safetyNode) {
                String safetyText = safety.asText();

                if (!safetyText.isBlank()) {
                    safetyList.add(safetyText);
                }
            }
        }

        return safetyList.isEmpty()
                ? List.of("No specific safety recommendations. Follow general safety guidelines.")
                : safetyList;
    }

    private List<String> extractNextWorkouts(JsonNode nextWorkoutsNode) {
        List<String> nextWorkoutsList = new ArrayList<>();

        if (nextWorkoutsNode.isArray()) {
            for (JsonNode nextWorkout : nextWorkoutsNode) {
                String workout = nextWorkout.path("workout").asText();
                String description = nextWorkout.path("description").asText();

                if (!workout.isBlank() || !description.isBlank()) {
                    nextWorkoutsList.add(String.format("%s: %s", workout, description));
                }
            }
        }

        return nextWorkoutsList.isEmpty()
                ? List.of("No workout suggestion.")
                : nextWorkoutsList;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvementList = new ArrayList<>();

        if (improvementsNode.isArray()) {
            for (JsonNode improvement : improvementsNode) {
                String area = improvement.path("area").asText();
                String recommendation = improvement.path("recommendation").asText();

                if (!area.isBlank() || !recommendation.isBlank()) {
                    improvementList.add(String.format("%s: %s", area, recommendation));
                }
            }
        }

        return improvementList.isEmpty()
                ? List.of("No specific improvements identified.")
                : improvementList;
    }

    private void addAnalysisSection(
            StringBuilder fullAnalysis,
            JsonNode analysisNode,
            String key,
            String title
    ) {
        String value = analysisNode.path(key).asText();

        if (!value.isBlank()) {
            fullAnalysis.append(title)
                    .append(": ")
                    .append(value)
                    .append("\n");
        }
    }

    private Recommendation buildDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType() != null ? activity.getType().name() : "UNKNOWN")
                .recommendation("Unable to generate recommendation at this time.")
                .improvements(List.of("N/A"))
                .suggestions(List.of("N/A"))
                .safetyMeasures(List.of("N/A"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
                You are an expert fitness coach and sports performance analyst.

                Analyze the following fitness activity and provide personalized recommendations.

                Activity Details:
                Activity Type: %s
                Duration Minutes: %d
                Calories Burned: %d
                Start Time: %s
                Additional Metrics: %s

                Instructions:
                1. Analyze overall performance.
                2. Evaluate workout duration.
                3. Evaluate calories burned.
                4. Analyze additional metrics such as distance, speed, heart rate, elevation, pace, or cadence if provided.
                5. Identify improvement areas.
                6. Suggest next workout activities.
                7. Include safety recommendations.

                Return ONLY valid JSON.
                Do NOT include markdown.
                Do NOT wrap the response in ```json.
                Do NOT include explanations outside the JSON.

                Expected JSON format:
                {
                  "analysis": {
                    "overall": "Overall performance analysis",
                    "duration": "Duration analysis",
                    "caloriesBurned": "Calories analysis",
                    "additionalMetrics": "Additional metrics analysis"
                  },
                  "improvements": [
                    {
                      "area": "Area to improve",
                      "recommendation": "Detailed recommendation"
                    }
                  ],
                  "nextWorkouts": [
                    {
                      "workout": "Workout name",
                      "description": "Workout description"
                    }
                  ],
                  "safety": [
                    "Safety recommendation 1",
                    "Safety recommendation 2"
                  ],
                  "summary": "Short overall summary"
                }
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getStartTime(),
                activity.getAdditionalMetrics()
        );
    }
}