package app.model.dto.meal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WellnessLogRequest {

    @Min(value = 1, message = "Mood score must be between 1 and 10")
    @Max(value = 10, message = "Mood score must be between 1 and 10")
    private int moodScore;

    @Min(value = 1, message = "Energy score must be between 1 and 10")
    @Max(value = 10, message = "Energy score must be between 1 and 10")
    private int energyScore;

    private String notes;
}
