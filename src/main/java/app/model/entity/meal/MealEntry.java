package app.model.entity.meal;

import app.model.entity.food.Food;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private int quantityInGrams;

    @ManyToOne(optional = false)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @ManyToOne(optional = false)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;
}
