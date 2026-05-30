package app.model.dto.meal;

import app.model.entity.meal.MealType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MealDetailsDto {

    private UUID id;
    private MealType mealType;
    private LocalDateTime eatenAt;

    private List<MealEntryDetailDto> foods;

    private double totalCalories;
    private double totalProtein;
    private double totalFat;
    private double totalCarbs;

    private Integer moodScore;
    private Integer energyScore;
    private String notes;
    private LocalDateTime loggedAt;
}
