package app.model.entity.food;

import app.model.entity.meal.MealEntry;
import app.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double caloriesPer100g;

    @Column(nullable = false)
    private double proteinPer100g;

    @Column(nullable = false)
    private double fatPer100g;

    @Column(nullable = false)
    private double carbsPer100g;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "food")
    @Builder.Default
    private List<MealEntry> entries = new ArrayList<>();
}
