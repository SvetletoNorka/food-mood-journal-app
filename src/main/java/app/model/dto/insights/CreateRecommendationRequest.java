package app.model.dto.insights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecommendationRequest {

    private UUID mealId;
    private Integer moodScore;
    private Integer energyScore;
    private List<String> foodNames;
    private Double totalCalories;
    private Double totalProtein;
    private Double totalFat;
    private Double totalCarbs;
}
