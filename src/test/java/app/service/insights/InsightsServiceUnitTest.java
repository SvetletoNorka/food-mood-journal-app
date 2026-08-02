package app.service.insights;

import app.client.insights.InsightsClient;
import app.model.dto.insights.CreateRecommendationRequest;
import app.model.dto.insights.RecommendationDto;
import app.model.dto.insights.RecommendationStatus;
import app.model.dto.insights.UpdateRecommendationRequest;
import app.model.dto.meal.MealDetailsDto;
import app.model.dto.meal.MealEntryDetailDto;
import app.model.entity.meal.MealType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightsServiceUnitTest {

    @Mock
    private InsightsClient insightsClient;

    @InjectMocks
    private InsightsService underTest;

    @Test
    void generateFromMeal_shouldSendFoodNamesAndMacros() {
        UUID userId = UUID.randomUUID();
        UUID mealId = UUID.randomUUID();
        MealDetailsDto meal = MealDetailsDto.builder()
                .id(mealId)
                .mealType(MealType.LUNCH)
                .eatenAt(LocalDateTime.now())
                .moodScore(7)
                .energyScore(6)
                .totalCalories(500)
                .totalProtein(30)
                .totalFat(20)
                .totalCarbs(40)
                .foods(List.of(MealEntryDetailDto.builder().foodName("Chicken").quantityInGrams(150).build()))
                .build();
        RecommendationDto expected = RecommendationDto.builder().id(UUID.randomUUID()).title("Tip").build();
        when(insightsClient.createRecommendation(eq(userId), any())).thenReturn(expected);

        RecommendationDto result = underTest.generateFromMeal(userId, meal);

        assertEquals("Tip", result.getTitle());
        ArgumentCaptor<CreateRecommendationRequest> captor = ArgumentCaptor.forClass(CreateRecommendationRequest.class);
        verify(insightsClient).createRecommendation(eq(userId), captor.capture());
        assertEquals(mealId, captor.getValue().getMealId());
        assertEquals(List.of("Chicken"), captor.getValue().getFoodNames());
        assertEquals(500.0, captor.getValue().getTotalCalories());
    }

    @Test
    void generateFromMeal_shouldUseEmptyFoodNames_whenFoodsNull() {
        UUID userId = UUID.randomUUID();
        MealDetailsDto meal = MealDetailsDto.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.SNACK)
                .eatenAt(LocalDateTime.now())
                .foods(null)
                .build();
        when(insightsClient.createRecommendation(eq(userId), any()))
                .thenReturn(RecommendationDto.builder().title("Empty").build());

        underTest.generateFromMeal(userId, meal);

        ArgumentCaptor<CreateRecommendationRequest> captor = ArgumentCaptor.forClass(CreateRecommendationRequest.class);
        verify(insightsClient).createRecommendation(eq(userId), captor.capture());
        assertTrue(captor.getValue().getFoodNames().isEmpty());
    }

    @Test
    void listForUser_shouldDelegateToClient() {
        UUID userId = UUID.randomUUID();
        List<RecommendationDto> expected = List.of(RecommendationDto.builder().title("A").build());
        when(insightsClient.getRecommendations(userId, RecommendationStatus.ACTIVE)).thenReturn(expected);

        assertEquals(expected, underTest.listForUser(userId, RecommendationStatus.ACTIVE));
    }

    @Test
    void apply_shouldUpdateStatusToApplied() {
        UUID id = UUID.randomUUID();
        when(insightsClient.updateRecommendation(eq(id), any()))
                .thenReturn(RecommendationDto.builder().status(RecommendationStatus.APPLIED).build());

        RecommendationDto result = underTest.apply(id);

        assertEquals(RecommendationStatus.APPLIED, result.getStatus());
        ArgumentCaptor<UpdateRecommendationRequest> captor = ArgumentCaptor.forClass(UpdateRecommendationRequest.class);
        verify(insightsClient).updateRecommendation(eq(id), captor.capture());
        assertEquals(RecommendationStatus.APPLIED, captor.getValue().getStatus());
    }

    @Test
    void dismiss_shouldUpdateStatusToDismissed() {
        UUID id = UUID.randomUUID();
        when(insightsClient.updateRecommendation(eq(id), any()))
                .thenReturn(RecommendationDto.builder().status(RecommendationStatus.DISMISSED).build());

        RecommendationDto result = underTest.dismiss(id);

        assertEquals(RecommendationStatus.DISMISSED, result.getStatus());
        ArgumentCaptor<UpdateRecommendationRequest> captor = ArgumentCaptor.forClass(UpdateRecommendationRequest.class);
        verify(insightsClient).updateRecommendation(eq(id), captor.capture());
        assertEquals(RecommendationStatus.DISMISSED, captor.getValue().getStatus());
    }
}
