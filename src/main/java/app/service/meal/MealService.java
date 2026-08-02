package app.service.meal;

import app.exception.FoodNotFoundException;
import app.exception.MealNotFoundException;
import app.exception.UserNotFoundException;
import app.mapper.meal.MealMapper;
import app.model.dto.meal.CreateMealRequest;
import app.model.dto.meal.MealDetailsDto;
import app.model.dto.meal.MealEntryRequest;
import app.model.dto.meal.WellnessLogRequest;
import app.model.entity.food.Food;
import app.model.entity.meal.Meal;
import app.model.entity.meal.MealEntry;
import app.model.entity.meal.WellnessLog;
import app.model.entity.user.User;
import app.repository.food.FoodRepository;
import app.repository.meal.MealRepository;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    public MealService(MealRepository mealRepository,
                       FoodRepository foodRepository,
                       UserRepository userRepository) {
        this.mealRepository = mealRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    public List<MealDetailsDto> findAllForUser(UUID ownerId) {
        List<MealDetailsDto> meals = mealRepository.findAllByOwnerIdOrderByEatenAtDesc(ownerId).stream()
                .map(MealMapper::toDetailsDto)
                .toList();
        log.info("Retrieved meals for ownerId={}, count={}", ownerId, meals.size());
        return meals;
    }

    public MealDetailsDto findById(UUID ownerId, UUID mealId) {
        Meal meal = getOwnedMeal(ownerId, mealId);
        log.info("Loaded meal id={} for ownerId={}", mealId, ownerId);
        return MealMapper.toDetailsDto(meal);
    }

    public MealDetailsDto create(UUID ownerId, CreateMealRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        Meal meal = Meal.builder()
                .mealType(request.getMealType())
                .eatenAt(request.getEatenAt())
                .owner(owner)
                .entries(new ArrayList<>())
                .build();

        for (MealEntryRequest entryRequest : request.getEntries()) {
            Food food = foodRepository.findByIdAndOwnerId(entryRequest.getFoodId(), ownerId)
                    .orElseThrow(FoodNotFoundException::new);

            MealEntry entry = MealEntry.builder()
                    .quantityInGrams(entryRequest.getQuantityInGrams())
                    .food(food)
                    .meal(meal)
                    .build();
            meal.getEntries().add(entry);
        }

        Meal saved = mealRepository.save(meal);
        log.info("Created meal id={} type={} for ownerId={}", saved.getId(), saved.getMealType(), ownerId);
        return MealMapper.toDetailsDto(saved);
    }

    public void saveWellnessLog(UUID ownerId, UUID mealId, WellnessLogRequest request) {
        Meal meal = getOwnedMeal(ownerId, mealId);

        if (meal.getWellnessLog() != null) {
            WellnessLog existing = meal.getWellnessLog();
            existing.setMoodScore(request.getMoodScore());
            existing.setEnergyScore(request.getEnergyScore());
            existing.setNotes(request.getNotes());
            log.info("Updated wellness log for mealId={} ownerId={}", mealId, ownerId);
            return;
        }

        WellnessLog wellnessLog = WellnessLog.builder()
                .moodScore(request.getMoodScore())
                .energyScore(request.getEnergyScore())
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .meal(meal)
                .build();

        meal.setWellnessLog(wellnessLog);
        mealRepository.save(meal);
        log.info("Created wellness log for mealId={} ownerId={}", mealId, ownerId);
    }

    private Meal getOwnedMeal(UUID ownerId, UUID mealId) {
        return mealRepository.findByIdAndOwnerId(mealId, ownerId)
                .orElseThrow(MealNotFoundException::new);
    }
}
