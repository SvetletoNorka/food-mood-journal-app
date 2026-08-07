package app.model.entity.food;

import app.model.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @Positive
    @Column(nullable = false)
    private double caloriesPer100g;

    @PositiveOrZero
    @Column(nullable = false)
    private double proteinPer100g;

    @PositiveOrZero
    @Column(nullable = false)
    private double fatPer100g;

    @PositiveOrZero
    @Column(nullable = false)
    private double carbsPer100g;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
