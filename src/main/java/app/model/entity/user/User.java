package app.model.entity.user;

import app.model.entity.food.Food;
import app.model.entity.meal.Meal;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(min = 3, max = 40)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdOn;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @OneToMany(mappedBy = "owner")
    @Builder.Default
    private List<Food> foods = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    @Builder.Default
    private List<Meal> meals = new ArrayList<>();
}
