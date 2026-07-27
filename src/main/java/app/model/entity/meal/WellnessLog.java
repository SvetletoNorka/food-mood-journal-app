package app.model.entity.meal;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wellness_logs")
public class WellnessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Min(1)
    @Max(10)
    @Column(nullable = false)
    private int moodScore;

    @Min(1)
    @Max(10)
    @Column(nullable = false)
    private int energyScore;

    @Size(max = 1000)
    private String notes;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @OneToOne(optional = false)
    @JoinColumn(name = "meal_id", nullable = false, unique = true)
    private Meal meal;
}
