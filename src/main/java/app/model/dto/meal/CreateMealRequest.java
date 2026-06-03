package app.model.dto.meal;

import app.model.entity.meal.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CreateMealRequest {

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Eaten at date and time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime eatenAt;

    @NotEmpty(message = "At least one food is required")
    @Valid
    @Builder.Default
    private List<MealEntryRequest> entries = new ArrayList<>();
}
