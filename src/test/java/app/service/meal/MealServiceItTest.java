package app.service.meal;

import app.model.dto.food.CreateFoodRequest;
import app.model.dto.meal.CreateMealRequest;
import app.model.dto.meal.MealDetailsDto;
import app.model.dto.meal.MealEntryRequest;
import app.model.dto.meal.WellnessLogRequest;
import app.model.entity.meal.MealType;
import app.service.food.FoodService;
import app.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static app.util.user.UserFactory.getUserRegisterRequest;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
class MealServiceItTest {

    @Autowired
    private MealService underTest;

    @Autowired
    private UserService userService;

    @Autowired
    private FoodService foodService;

    @Test
    void createMealAndWellnessLog_shouldPersistAndMapTotals() {
        UUID ownerId = userService.register(getUserRegisterRequest()).getId();
        UUID foodId = foodService.create(ownerId, CreateFoodRequest.builder()
                .name("Chicken")
                .caloriesPer100g(165)
                .proteinPer100g(31)
                .fatPer100g(3.6)
                .carbsPer100g(0)
                .build()).getId();

        MealDetailsDto created = underTest.create(ownerId, CreateMealRequest.builder()
                .mealType(MealType.DINNER)
                .eatenAt(LocalDateTime.of(2026, 8, 1, 19, 0))
                .entries(List.of(MealEntryRequest.builder()
                        .foodId(foodId)
                        .quantityInGrams(200)
                        .build()))
                .build());

        assertEquals(MealType.DINNER, created.getMealType());
        assertEquals(330, created.getTotalCalories());
        assertEquals(62, created.getTotalProtein());

        underTest.saveWellnessLog(ownerId, created.getId(), WellnessLogRequest.builder()
                .moodScore(8)
                .energyScore(7)
                .notes("Solid dinner")
                .build());

        MealDetailsDto withWellness = underTest.findById(ownerId, created.getId());
        assertEquals(8, withWellness.getMoodScore());
        assertEquals(7, withWellness.getEnergyScore());
        assertEquals("Solid dinner", withWellness.getNotes());
        assertEquals(1, underTest.findAllForUser(ownerId).size());
    }
}
