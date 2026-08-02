package app.service.food;

import app.model.dto.food.CreateFoodRequest;
import app.model.dto.food.EditFoodRequest;
import app.model.dto.food.FoodDto;
import app.model.dto.user.UserRegisterRequest;
import app.repository.food.FoodRepository;
import app.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static app.util.user.UserFactory.getUserRegisterRequest;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
class FoodServiceItTest {

    @Autowired
    private FoodService underTest;

    @Autowired
    private UserService userService;

    @Autowired
    private FoodRepository foodRepository;

    @Test
    void createFindUpdateDelete_shouldWorkEndToEnd() {
        UUID ownerId = userService.register(getUserRegisterRequest()).getId();

        FoodDto created = underTest.create(ownerId, CreateFoodRequest.builder()
                .name("Yogurt")
                .caloriesPer100g(59)
                .proteinPer100g(10)
                .fatPer100g(0.4)
                .carbsPer100g(3.6)
                .build());

        List<FoodDto> foods = underTest.findAllForUser(ownerId);
        assertEquals(1, foods.size());
        assertEquals("Yogurt", underTest.findById(ownerId, created.getId()).getName());

        underTest.update(ownerId, created.getId(), EditFoodRequest.builder()
                .name("Greek Yogurt")
                .caloriesPer100g(97)
                .proteinPer100g(9)
                .fatPer100g(5)
                .carbsPer100g(3.6)
                .build());

        assertEquals("Greek Yogurt", underTest.findById(ownerId, created.getId()).getName());

        underTest.delete(ownerId, created.getId());
        assertTrue(foodRepository.findById(created.getId()).isEmpty());
    }

    @Test
    void findAllForUser_shouldReturnOnlyOwnerFoods() {
        UUID ownerA = userService.register(getUserRegisterRequest()).getId();
        UserRegisterRequest other = UserRegisterRequest.builder()
                .username("OtherUser")
                .email("other@example.com")
                .password("Password")
                .build();
        UUID ownerB = userService.register(other).getId();

        underTest.create(ownerA, CreateFoodRequest.builder()
                .name("Apple")
                .caloriesPer100g(52)
                .proteinPer100g(0.3)
                .fatPer100g(0.2)
                .carbsPer100g(14)
                .build());
        underTest.create(ownerB, CreateFoodRequest.builder()
                .name("Orange")
                .caloriesPer100g(47)
                .proteinPer100g(0.9)
                .fatPer100g(0.1)
                .carbsPer100g(12)
                .build());

        assertEquals(1, underTest.findAllForUser(ownerA).size());
        assertEquals("Apple", underTest.findAllForUser(ownerA).get(0).getName());
    }
}
