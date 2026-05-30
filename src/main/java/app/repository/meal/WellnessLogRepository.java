package app.repository.meal;

import app.model.entity.meal.WellnessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WellnessLogRepository extends JpaRepository<WellnessLog, UUID> {

    Optional<WellnessLog> findByMealId(UUID mealId);

    boolean existsByMealId(UUID mealId);
}
