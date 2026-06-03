package app.mapper.meal;

import app.model.dto.meal.MealDetailsDto;
import app.model.dto.meal.MealEntryDetailDto;
import app.model.entity.meal.Meal;
import app.model.entity.meal.MealEntry;
import app.model.entity.meal.WellnessLog;

import java.util.List;

public final class MealMapper {

    private MealMapper() {
    }

    public static MealDetailsDto toDetailsDto(Meal meal) {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbs = 0;

        List<MealEntryDetailDto> foods = meal.getEntries().stream()
                .map(entry -> {
                    double factor = entry.getQuantityInGrams() / 100.0;
                    double calories = entry.getFood().getCaloriesPer100g() * factor;
                    double protein = entry.getFood().getProteinPer100g() * factor;
                    double fat = entry.getFood().getFatPer100g() * factor;
                    double carbs = entry.getFood().getCarbsPer100g() * factor;

                    return MealEntryDetailDto.builder()
                            .foodName(entry.getFood().getName())
                            .quantityInGrams(entry.getQuantityInGrams())
                            .calories(round(calories))
                            .protein(round(protein))
                            .fat(round(fat))
                            .carbs(round(carbs))
                            .build();
                })
                .toList();

        for (MealEntry entry : meal.getEntries()) {
            double factor = entry.getQuantityInGrams() / 100.0;
            totalCalories += entry.getFood().getCaloriesPer100g() * factor;
            totalProtein += entry.getFood().getProteinPer100g() * factor;
            totalFat += entry.getFood().getFatPer100g() * factor;
            totalCarbs += entry.getFood().getCarbsPer100g() * factor;
        }

        MealDetailsDto.MealDetailsDtoBuilder builder = MealDetailsDto.builder()
                .id(meal.getId())
                .mealType(meal.getMealType())
                .eatenAt(meal.getEatenAt())
                .foods(foods)
                .totalCalories(round(totalCalories))
                .totalProtein(round(totalProtein))
                .totalFat(round(totalFat))
                .totalCarbs(round(totalCarbs));

        WellnessLog wellnessLog = meal.getWellnessLog();
        if (wellnessLog != null) {
            builder.moodScore(wellnessLog.getMoodScore())
                    .energyScore(wellnessLog.getEnergyScore())
                    .notes(wellnessLog.getNotes())
                    .loggedAt(wellnessLog.getCreatedAt());
        }

        return builder.build();
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
