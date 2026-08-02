package app.service.meal;

import app.exception.FoodNotFoundException;
import app.exception.MealNotFoundException;
import app.exception.UserNotFoundException;
import app.model.dto.meal.CreateMealRequest;
import app.model.dto.meal.MealDetailsDto;
import app.model.dto.meal.MealEntryRequest;
import app.model.dto.meal.WellnessLogRequest;
import app.model.entity.food.Food;
import app.model.entity.meal.Meal;
import app.model.entity.meal.MealType;
import app.model.entity.meal.WellnessLog;
import app.model.entity.user.User;
import app.repository.food.FoodRepository;
import app.repository.meal.MealRepository;
import app.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static app.util.user.UserFactory.getUserEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceUnitTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MealService underTest;

    @Test
    void findAllForUser_shouldReturnMappedMeals() {
        User owner = getUserEntity();
        Meal meal = sampleMeal(owner);
        when(mealRepository.findAllByOwnerIdOrderByEatenAtDesc(owner.getId())).thenReturn(List.of(meal));

        List<MealDetailsDto> result = underTest.findAllForUser(owner.getId());

        assertEquals(1, result.size());
        assertEquals(MealType.BREAKFAST, result.get(0).getMealType());
    }

    @Test
    void findById_shouldReturnMeal_whenOwned() {
        User owner = getUserEntity();
        Meal meal = sampleMeal(owner);
        when(mealRepository.findByIdAndOwnerId(meal.getId(), owner.getId())).thenReturn(Optional.of(meal));

        MealDetailsDto result = underTest.findById(owner.getId(), meal.getId());

        assertEquals(meal.getId(), result.getId());
        assertEquals(1, result.getFoods().size());
    }

    @Test
    void findById_shouldThrow_whenMealMissing() {
        UUID ownerId = UUID.randomUUID();
        UUID mealId = UUID.randomUUID();
        when(mealRepository.findByIdAndOwnerId(mealId, ownerId)).thenReturn(Optional.empty());

        assertThrows(MealNotFoundException.class, () -> underTest.findById(ownerId, mealId));
    }

    @Test
    void create_shouldSaveMealWithEntries() {
        User owner = getUserEntity();
        Food food = Food.builder()
                .id(UUID.randomUUID())
                .name("Oats")
                .caloriesPer100g(389)
                .proteinPer100g(17)
                .fatPer100g(7)
                .carbsPer100g(66)
                .owner(owner)
                .build();
        CreateMealRequest request = CreateMealRequest.builder()
                .mealType(MealType.BREAKFAST)
                .eatenAt(LocalDateTime.now())
                .entries(List.of(MealEntryRequest.builder()
                        .foodId(food.getId())
                        .quantityInGrams(100)
                        .build()))
                .build();

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(foodRepository.findByIdAndOwnerId(food.getId(), owner.getId())).thenReturn(Optional.of(food));
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> {
            Meal meal = invocation.getArgument(0);
            meal.setId(UUID.randomUUID());
            return meal;
        });

        MealDetailsDto result = underTest.create(owner.getId(), request);

        assertEquals(MealType.BREAKFAST, result.getMealType());
        assertEquals(389, result.getTotalCalories());
        verify(mealRepository).save(any(Meal.class));
    }

    @Test
    void create_shouldThrow_whenOwnerMissing() {
        UUID ownerId = UUID.randomUUID();
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        CreateMealRequest request = CreateMealRequest.builder()
                .mealType(MealType.LUNCH)
                .eatenAt(LocalDateTime.now())
                .entries(List.of(MealEntryRequest.builder()
                        .foodId(UUID.randomUUID())
                        .quantityInGrams(50)
                        .build()))
                .build();

        assertThrows(UserNotFoundException.class, () -> underTest.create(ownerId, request));
    }

    @Test
    void create_shouldThrow_whenFoodMissing() {
        User owner = getUserEntity();
        UUID foodId = UUID.randomUUID();
        CreateMealRequest request = CreateMealRequest.builder()
                .mealType(MealType.DINNER)
                .eatenAt(LocalDateTime.now())
                .entries(List.of(MealEntryRequest.builder()
                        .foodId(foodId)
                        .quantityInGrams(50)
                        .build()))
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(foodRepository.findByIdAndOwnerId(foodId, owner.getId())).thenReturn(Optional.empty());

        assertThrows(FoodNotFoundException.class, () -> underTest.create(owner.getId(), request));
    }

    @Test
    void saveWellnessLog_shouldCreateNewLog_whenMissing() {
        User owner = getUserEntity();
        Meal meal = sampleMeal(owner);
        meal.setWellnessLog(null);
        when(mealRepository.findByIdAndOwnerId(meal.getId(), owner.getId())).thenReturn(Optional.of(meal));
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WellnessLogRequest request = WellnessLogRequest.builder()
                .moodScore(8)
                .energyScore(7)
                .notes("Great")
                .build();

        underTest.saveWellnessLog(owner.getId(), meal.getId(), request);

        assertNotNull(meal.getWellnessLog());
        assertEquals(8, meal.getWellnessLog().getMoodScore());
        verify(mealRepository).save(meal);
    }

    @Test
    void saveWellnessLog_shouldUpdateExistingLog() {
        User owner = getUserEntity();
        Meal meal = sampleMeal(owner);
        WellnessLog existing = WellnessLog.builder()
                .id(UUID.randomUUID())
                .moodScore(3)
                .energyScore(3)
                .notes("old")
                .createdAt(LocalDateTime.now())
                .meal(meal)
                .build();
        meal.setWellnessLog(existing);
        when(mealRepository.findByIdAndOwnerId(meal.getId(), owner.getId())).thenReturn(Optional.of(meal));

        WellnessLogRequest request = WellnessLogRequest.builder()
                .moodScore(9)
                .energyScore(8)
                .notes("updated")
                .build();

        underTest.saveWellnessLog(owner.getId(), meal.getId(), request);

        assertEquals(9, existing.getMoodScore());
        assertEquals(8, existing.getEnergyScore());
        assertEquals("updated", existing.getNotes());
        verify(mealRepository, never()).save(any());
    }

    private Meal sampleMeal(User owner) {
        Food food = Food.builder()
                .id(UUID.randomUUID())
                .name("Eggs")
                .caloriesPer100g(155)
                .proteinPer100g(13)
                .fatPer100g(11)
                .carbsPer100g(1.1)
                .owner(owner)
                .build();
        Meal meal = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.BREAKFAST)
                .eatenAt(LocalDateTime.now())
                .owner(owner)
                .entries(new ArrayList<>())
                .build();
        meal.getEntries().add(app.model.entity.meal.MealEntry.builder()
                .id(UUID.randomUUID())
                .quantityInGrams(100)
                .food(food)
                .meal(meal)
                .build());
        return meal;
    }
}
