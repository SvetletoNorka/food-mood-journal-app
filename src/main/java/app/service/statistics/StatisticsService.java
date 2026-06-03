package app.service.statistics;

import app.model.dto.meal.MealDetailsDto;
import app.model.dto.statistics.StatisticsPageDto;
import app.model.dto.statistics.StatisticsSort;
import app.service.meal.MealService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class StatisticsService {

    private final MealService mealService;

    public StatisticsService(MealService mealService) {
        this.mealService = mealService;
    }

    public StatisticsPageDto buildPage(UUID ownerId, StatisticsSort sort) {
        List<MealDetailsDto> allMeals = mealService.findAllForUser(ownerId);

        List<MealDetailsDto> topFive = allMeals.stream()
                .sorted(comparatorFor(sort).reversed())
                .limit(5)
                .toList();

        double averageMood = allMeals.stream()
                .filter(meal -> meal.getMoodScore() != null)
                .mapToInt(MealDetailsDto::getMoodScore)
                .average()
                .orElse(0);

        double averageEnergy = allMeals.stream()
                .filter(meal -> meal.getEnergyScore() != null)
                .mapToInt(MealDetailsDto::getEnergyScore)
                .average()
                .orElse(0);

        return StatisticsPageDto.builder()
                .allMeals(allMeals)
                .topFive(topFive)
                .sort(sort)
                .averageMood(round(averageMood))
                .averageEnergy(round(averageEnergy))
                .build();
    }

    private Comparator<MealDetailsDto> comparatorFor(StatisticsSort sort) {
        return switch (sort) {
            case MOOD -> Comparator.comparing(meal -> meal.getMoodScore() != null ? meal.getMoodScore() : 0);
            case ENERGY -> Comparator.comparing(meal -> meal.getEnergyScore() != null ? meal.getEnergyScore() : 0);
            case PROTEIN -> Comparator.comparing(MealDetailsDto::getTotalProtein);
            case FAT -> Comparator.comparing(MealDetailsDto::getTotalFat);
            case CARBS -> Comparator.comparing(MealDetailsDto::getTotalCarbs);
        };
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
