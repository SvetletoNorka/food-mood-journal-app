package app.model.dto.meal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MealEntryDetailDto {

    private String foodName;
    private int quantityInGrams;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
}
