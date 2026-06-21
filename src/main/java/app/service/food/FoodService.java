package app.service.food;

import app.exception.FoodNotFoundException;
import app.exception.UserNotFoundException;
import app.mapper.food.FoodMapper;
import app.model.dto.food.CreateFoodRequest;
import app.model.dto.food.EditFoodRequest;
import app.model.dto.food.FoodDto;
import app.model.entity.food.Food;
import app.model.entity.user.User;
import app.repository.food.FoodRepository;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FoodService {

    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public FoodService(FoodRepository foodRepository, UserRepository userRepository) {
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    public List<FoodDto> findAllForUser(UUID ownerId) {
        return foodRepository.findAllByOwnerIdOrderByNameAsc(ownerId).stream()
                .map(FoodMapper::toDto)
                .toList();
    }

    public FoodDto create(UUID ownerId, CreateFoodRequest request) {
        User owner = getOwner(ownerId);
        Food food = foodRepository.save(FoodMapper.toEntity(request, owner));
        return FoodMapper.toDto(food);
    }

    public FoodDto findById(UUID ownerId, UUID foodId) {
        Food food = getOwnedFood(ownerId, foodId);
        return FoodMapper.toDto(food);
    }

    public void update(UUID ownerId, UUID foodId, EditFoodRequest request) {
        Food food = getOwnedFood(ownerId, foodId);
        FoodMapper.updateEntity(food, request);
        foodRepository.save(food);
    }

    public void delete(UUID ownerId, UUID foodId) {
        Food food = getOwnedFood(ownerId, foodId);
        foodRepository.delete(food);
    }

    private Food getOwnedFood(UUID ownerId, UUID foodId) {
        return foodRepository.findByIdAndOwnerId(foodId, ownerId)
                .orElseThrow(FoodNotFoundException::new);
    }

    private User getOwner(UUID ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));
    }
}
