package app.model.entity.meal;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private int moodScore;

    @Column(nullable = false)
    private int energyScore;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(optional = false)
    @JoinColumn(name = "meal_id", nullable = false, unique = true)
    private Meal meal;
}
