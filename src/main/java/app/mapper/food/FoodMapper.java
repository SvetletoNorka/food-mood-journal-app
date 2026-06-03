package app.mapper.food;

import app.model.dto.food.CreateFoodRequest;
import app.model.dto.food.EditFoodRequest;
import app.model.dto.food.FoodDto;
import app.model.entity.food.Food;
import app.model.entity.user.User;

public final class FoodMapper {

    private FoodMapper() {
    }

    public static FoodDto toDto(Food food) {
        return FoodDto.builder()
                .id(food.getId())
                .name(food.getName())
                .caloriesPer100g(food.getCaloriesPer100g())
                .proteinPer100g(food.getProteinPer100g())
                .fatPer100g(food.getFatPer100g())
                .carbsPer100g(food.getCarbsPer100g())
                .build();
    }

    public static Food toEntity(CreateFoodRequest request, User owner) {
        return Food.builder()
                .name(request.getName())
                .caloriesPer100g(request.getCaloriesPer100g())
                .proteinPer100g(request.getProteinPer100g())
                .fatPer100g(request.getFatPer100g())
                .carbsPer100g(request.getCarbsPer100g())
                .owner(owner)
                .build();
    }

    public static void updateEntity(Food food, EditFoodRequest request) {
        food.setName(request.getName());
        food.setCaloriesPer100g(request.getCaloriesPer100g());
        food.setProteinPer100g(request.getProteinPer100g());
        food.setFatPer100g(request.getFatPer100g());
        food.setCarbsPer100g(request.getCarbsPer100g());
    }
}
