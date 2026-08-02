package app.mapper;

import app.mapper.food.FoodMapper;
import app.mapper.meal.MealMapper;
import app.mapper.user.UserMapper;
import app.model.dto.food.CreateFoodRequest;
import app.model.dto.food.EditFoodRequest;
import app.model.dto.food.FoodDto;
import app.model.dto.meal.MealDetailsDto;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.food.Food;
import app.model.entity.meal.Meal;
import app.model.entity.meal.MealEntry;
import app.model.entity.meal.MealType;
import app.model.entity.meal.WellnessLog;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static app.util.user.UserFactory.getUserEntity;
import static app.util.user.UserFactory.getUserRegisterRequest;
import static org.junit.jupiter.api.Assertions.*;

class MapperUnitTest {

    @Test
    void userMapper_shouldMapEntityAndDto() {
        UserRegisterRequest request = getUserRegisterRequest();
        User entity = UserMapper.toUserEntity(request);

        assertNotNull(entity);
        assertEquals(request.getUsername(), entity.getUsername());
        assertEquals(UserRole.USER, entity.getRole());
        assertTrue(entity.isActive());
        assertNull(UserMapper.toUserEntity(null));
        assertNull(UserMapper.toUserDto(null));

        entity.setId(UUID.randomUUID());
        UserDto dto = UserMapper.toUserDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getEmail(), dto.getEmail());
    }

    @Test
    void foodMapper_shouldMapCreateUpdateAndDto() {
        User owner = getUserEntity();
        CreateFoodRequest create = CreateFoodRequest.builder()
                .name("Bread")
                .caloriesPer100g(265)
                .proteinPer100g(9)
                .fatPer100g(3)
                .carbsPer100g(49)
                .build();

        Food food = FoodMapper.toEntity(create, owner);
        food.setId(UUID.randomUUID());
        FoodDto dto = FoodMapper.toDto(food);

        assertEquals("Bread", dto.getName());
        assertEquals(owner, food.getOwner());

        FoodMapper.updateEntity(food, EditFoodRequest.builder()
                .name("Toast")
                .caloriesPer100g(300)
                .proteinPer100g(10)
                .fatPer100g(4)
                .carbsPer100g(50)
                .build());
        assertEquals("Toast", food.getName());
        assertEquals(300, food.getCaloriesPer100g());
    }

    @Test
    void mealMapper_shouldComputeTotalsAndWellness() {
        User owner = getUserEntity();
        Food food = Food.builder()
                .id(UUID.randomUUID())
                .name("Pasta")
                .caloriesPer100g(131)
                .proteinPer100g(5)
                .fatPer100g(1.1)
                .carbsPer100g(25)
                .owner(owner)
                .build();
        Meal meal = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.LUNCH)
                .eatenAt(LocalDateTime.now())
                .owner(owner)
                .entries(new ArrayList<>())
                .build();
        meal.getEntries().add(MealEntry.builder()
                .quantityInGrams(200)
                .food(food)
                .meal(meal)
                .build());
        meal.setWellnessLog(WellnessLog.builder()
                .moodScore(6)
                .energyScore(5)
                .notes("ok")
                .createdAt(LocalDateTime.now())
                .meal(meal)
                .build());

        MealDetailsDto dto = MealMapper.toDetailsDto(meal);

        assertEquals(262, dto.getTotalCalories());
        assertEquals(10, dto.getTotalProtein());
        assertEquals(6, dto.getMoodScore());
        assertEquals(5, dto.getEnergyScore());
        assertEquals("Pasta", dto.getFoods().get(0).getFoodName());
    }
}
