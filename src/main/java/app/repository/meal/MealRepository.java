package app.repository.meal;

import app.model.entity.meal.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {

    List<Meal> findAllByOwnerIdOrderByEatenAtDesc(UUID ownerId);

    Optional<Meal> findByIdAndOwnerId(UUID id, UUID ownerId);
}
