package app.model.dto.statistics;

import app.model.dto.meal.MealDetailsDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatisticsPageDto {

    private List<MealDetailsDto> allMeals;
    private List<MealDetailsDto> topFive;
    private StatisticsSort sort;
    private double averageMood;
    private double averageEnergy;
}
