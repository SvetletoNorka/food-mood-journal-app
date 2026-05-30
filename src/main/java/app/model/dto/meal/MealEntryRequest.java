package app.model.dto.meal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MealEntryRequest {

    @NotNull(message = "Food is required")
    private UUID foodId;

    @Min(value = 1, message = "Quantity must be at least 1 gram")
    private int quantityInGrams;
}
