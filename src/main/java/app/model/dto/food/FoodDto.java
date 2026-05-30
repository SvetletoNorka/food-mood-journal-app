package app.model.dto.food;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FoodDto {

    private UUID id;
    private String name;
    private double caloriesPer100g;
    private double proteinPer100g;
    private double fatPer100g;
    private double carbsPer100g;
}
