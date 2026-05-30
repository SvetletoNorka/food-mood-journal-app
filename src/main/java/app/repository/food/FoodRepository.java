package app.repository.food;

import app.model.entity.food.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FoodRepository extends JpaRepository<Food, UUID> {

    List<Food> findAllByOwnerIdOrderByNameAsc(UUID ownerId);

    Optional<Food> findByIdAndOwnerId(UUID id, UUID ownerId);
}
