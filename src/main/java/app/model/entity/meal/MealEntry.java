package app.model.entity.meal;

import app.model.entity.food.Food;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meal_entries")
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Min(1)
    @Column(nullable = false)
    private int quantityInGrams;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;
}
