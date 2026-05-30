package app.model.dto.food;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateFoodRequest {

    @NotBlank
    @Size(min = 2, message = "Name must be at least 2 characters")
    private String name;

    @Positive(message = "Calories must be greater than 0")
    private double caloriesPer100g;

    @PositiveOrZero(message = "Protein must be zero or positive")
    private double proteinPer100g;

    @PositiveOrZero(message = "Fat must be zero or positive")
    private double fatPer100g;

    @PositiveOrZero(message = "Carbs must be zero or positive")
    private double carbsPer100g;
}
