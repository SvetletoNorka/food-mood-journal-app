package app.service.insights;

import app.client.insights.InsightsClient;
import app.model.dto.insights.CreateRecommendationRequest;
import app.model.dto.insights.RecommendationDto;
import app.model.dto.insights.RecommendationStatus;
import app.model.dto.insights.UpdateRecommendationRequest;
import app.model.dto.meal.MealDetailsDto;
import app.model.dto.meal.MealEntryDetailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightsService {

    private final InsightsClient insightsClient;

    public RecommendationDto generateFromMeal(UUID userId, MealDetailsDto meal) {
        List<String> foodNames = meal.getFoods() == null
                ? List.of()
                : meal.getFoods().stream()
                .map(MealEntryDetailDto::getFoodName)
                .toList();

        CreateRecommendationRequest request = CreateRecommendationRequest.builder()
                .mealId(meal.getId())
                .moodScore(meal.getMoodScore())
                .energyScore(meal.getEnergyScore())
                .foodNames(foodNames)
                .totalCalories(meal.getTotalCalories())
                .totalProtein(meal.getTotalProtein())
                .totalFat(meal.getTotalFat())
                .totalCarbs(meal.getTotalCarbs())
                .build();

        RecommendationDto recommendation = insightsClient.createRecommendation(userId, request);
        log.info("Generated recommendation id={} for userId={} from mealId={}",
                recommendation.getId(), userId, meal.getId());
        return recommendation;
    }

    public List<RecommendationDto> listForUser(UUID userId, RecommendationStatus status) {
        List<RecommendationDto> recommendations = insightsClient.getRecommendations(userId, status);
        log.info("Retrieved recommendations for userId={}, status={}, count={}",
                userId, status, recommendations.size());
        return recommendations;
    }

    public RecommendationDto apply(UUID recommendationId) {
        RecommendationDto recommendation = insightsClient.updateRecommendation(
                recommendationId,
                UpdateRecommendationRequest.builder().status(RecommendationStatus.APPLIED).build());
        log.info("Applied recommendation id={}", recommendationId);
        return recommendation;
    }

    public RecommendationDto dismiss(UUID recommendationId) {
        RecommendationDto recommendation = insightsClient.updateRecommendation(
                recommendationId,
                UpdateRecommendationRequest.builder().status(RecommendationStatus.DISMISSED).build());
        log.info("Dismissed recommendation id={}", recommendationId);
        return recommendation;
    }
}
