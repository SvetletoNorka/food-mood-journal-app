package app.service.insights;

import app.client.insights.InsightsClient;
import app.model.dto.insights.CreateRecommendationRequest;
import app.model.dto.insights.RecommendationDto;
import app.model.dto.insights.RecommendationStatus;
import app.model.dto.insights.UpdateRecommendationRequest;
import app.model.dto.meal.MealDetailsDto;
import app.model.dto.meal.MealEntryDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

        return insightsClient.createRecommendation(userId, request);
    }

    public List<RecommendationDto> listForUser(UUID userId, RecommendationStatus status) {
        return insightsClient.getRecommendations(userId, status);
    }

    public RecommendationDto apply(UUID recommendationId) {
        return insightsClient.updateRecommendation(
                recommendationId,
                UpdateRecommendationRequest.builder().status(RecommendationStatus.APPLIED).build());
    }

    public RecommendationDto dismiss(UUID recommendationId) {
        return insightsClient.updateRecommendation(
                recommendationId,
                UpdateRecommendationRequest.builder().status(RecommendationStatus.DISMISSED).build());
    }
}
