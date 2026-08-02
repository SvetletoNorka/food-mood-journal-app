package app.service.food;

import app.exception.FoodNotFoundException;
import app.exception.UserNotFoundException;
import app.model.dto.food.CreateFoodRequest;
import app.model.dto.food.EditFoodRequest;
import app.model.dto.food.FoodDto;
import app.model.entity.food.Food;
import app.model.entity.user.User;
import app.repository.food.FoodRepository;
import app.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static app.util.user.UserFactory.getUserEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceUnitTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FoodService underTest;

    @Test
    void findAllForUser_shouldReturnMappedFoods() {
        User owner = getUserEntity();
        Food food = Food.builder()
                .id(UUID.randomUUID())
                .name("Apple")
                .caloriesPer100g(52)
                .proteinPer100g(0.3)
                .fatPer100g(0.2)
                .carbsPer100g(14)
                .owner(owner)
                .build();
        when(foodRepository.findAllByOwnerIdOrderByNameAsc(owner.getId())).thenReturn(List.of(food));

        List<FoodDto> result = underTest.findAllForUser(owner.getId());

        assertEquals(1, result.size());
        assertEquals("Apple", result.get(0).getName());
    }

    @Test
    void create_shouldSaveFood_whenOwnerExists() {
        User owner = getUserEntity();
        CreateFoodRequest request = CreateFoodRequest.builder()
                .name("Rice")
                .caloriesPer100g(130)
                .proteinPer100g(2.7)
                .fatPer100g(0.3)
                .carbsPer100g(28)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(foodRepository.save(any(Food.class))).thenAnswer(invocation -> {
            Food food = invocation.getArgument(0);
            food.setId(UUID.randomUUID());
            return food;
        });

        FoodDto result = underTest.create(owner.getId(), request);

        assertEquals("Rice", result.getName());
        assertEquals(130, result.getCaloriesPer100g());
        verify(foodRepository).save(any(Food.class));
    }

    @Test
    void create_shouldThrow_whenOwnerMissing() {
        UUID ownerId = UUID.randomUUID();
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        CreateFoodRequest request = CreateFoodRequest.builder()
                .name("Rice")
                .caloriesPer100g(130)
                .build();

        assertThrows(UserNotFoundException.class, () -> underTest.create(ownerId, request));
    }

    @Test
    void findById_shouldReturnFood_whenOwned() {
        User owner = getUserEntity();
        UUID foodId = UUID.randomUUID();
        Food food = Food.builder()
                .id(foodId)
                .name("Banana")
                .caloriesPer100g(89)
                .proteinPer100g(1.1)
                .fatPer100g(0.3)
                .carbsPer100g(23)
                .owner(owner)
                .build();
        when(foodRepository.findByIdAndOwnerId(foodId, owner.getId())).thenReturn(Optional.of(food));

        FoodDto result = underTest.findById(owner.getId(), foodId);

        assertEquals("Banana", result.getName());
    }

    @Test
    void findById_shouldThrow_whenFoodMissing() {
        UUID ownerId = UUID.randomUUID();
        UUID foodId = UUID.randomUUID();
        when(foodRepository.findByIdAndOwnerId(foodId, ownerId)).thenReturn(Optional.empty());

        assertThrows(FoodNotFoundException.class, () -> underTest.findById(ownerId, foodId));
    }

    @Test
    void update_shouldModifyOwnedFood() {
        User owner = getUserEntity();
        UUID foodId = UUID.randomUUID();
        Food food = Food.builder()
                .id(foodId)
                .name("Old")
                .caloriesPer100g(10)
                .proteinPer100g(1)
                .fatPer100g(1)
                .carbsPer100g(1)
                .owner(owner)
                .build();
        when(foodRepository.findByIdAndOwnerId(foodId, owner.getId())).thenReturn(Optional.of(food));
        when(foodRepository.save(any(Food.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EditFoodRequest request = EditFoodRequest.builder()
                .name("New")
                .caloriesPer100g(20)
                .proteinPer100g(2)
                .fatPer100g(3)
                .carbsPer100g(4)
                .build();

        underTest.update(owner.getId(), foodId, request);

        assertEquals("New", food.getName());
        assertEquals(20, food.getCaloriesPer100g());
        verify(foodRepository).save(food);
    }

    @Test
    void delete_shouldRemoveOwnedFood() {
        User owner = getUserEntity();
        UUID foodId = UUID.randomUUID();
        Food food = Food.builder().id(foodId).name("DeleteMe").owner(owner).caloriesPer100g(1).build();
        when(foodRepository.findByIdAndOwnerId(foodId, owner.getId())).thenReturn(Optional.of(food));

        underTest.delete(owner.getId(), foodId);

        verify(foodRepository).delete(food);
    }
}
