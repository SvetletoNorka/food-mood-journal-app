package app.web.food;

import app.config.UserSession;
import app.model.dto.food.CreateFoodRequest;
import app.model.dto.food.EditFoodRequest;
import app.model.dto.food.FoodDto;
import app.service.food.FoodService;
import app.web.SessionGuard;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/foods")
public class FoodController {

    private final FoodService foodService;
    private final UserSession userSession;

    public FoodController(FoodService foodService, UserSession userSession) {
        this.foodService = foodService;
        this.userSession = userSession;
    }

    @GetMapping
    public ModelAndView listFoods() {
        ModelAndView redirect = SessionGuard.redirectToLoginIfNeeded(userSession);
        if (redirect != null) {
            return redirect;
        }

        ModelAndView modelAndView = new ModelAndView("foods");
        modelAndView.addObject("foods", foodService.findAllForUser(userSession.getId()));
        modelAndView.addObject("activePage", "foods");
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView newFoodForm() {
        ModelAndView redirect = SessionGuard.redirectToLoginIfNeeded(userSession);
        if (redirect != null) {
            return redirect;
        }

        ModelAndView modelAndView = new ModelAndView("food-form");
        modelAndView.addObject("createFoodRequest", CreateFoodRequest.builder().build());
        modelAndView.addObject("activePage", "foods");
        modelAndView.addObject("isEdit", false);
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createFood(@Valid @ModelAttribute("createFoodRequest") CreateFoodRequest createFoodRequest,
                                 BindingResult bindingResult) {
        ModelAndView redirect = SessionGuard.redirectToLoginIfNeeded(userSession);
        if (redirect != null) {
            return redirect;
        }

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("food-form");
            modelAndView.addObject("activePage", "foods");
            modelAndView.addObject("isEdit", false);
            return modelAndView;
        }

        foodService.create(userSession.getId(), createFoodRequest);
        return new ModelAndView("redirect:/foods");
    }

    @GetMapping("/{id}/edit")
    public ModelAndView editFoodForm(@PathVariable UUID id) {
        ModelAndView redirect = SessionGuard.redirectToLoginIfNeeded(userSession);
        if (redirect != null) {
            return redirect;
        }

        FoodDto food = foodService.findById(userSession.getId(), id);
        EditFoodRequest editFoodRequest = EditFoodRequest.builder()
                .name(food.getName())
                .caloriesPer100g(food.getCaloriesPer100g())
                .proteinPer100g(food.getProteinPer100g())
                .fatPer100g(food.getFatPer100g())
                .carbsPer100g(food.getCarbsPer100g())
                .build();

        ModelAndView modelAndView = new ModelAndView("food-form");
        modelAndView.addObject("foodId", id);
        modelAndView.addObject("editFoodRequest", editFoodRequest);
        modelAndView.addObject("activePage", "foods");
        modelAndView.addObject("isEdit", true);
        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView updateFood(@PathVariable UUID id,
                                   @Valid @ModelAttribute("editFoodRequest") EditFoodRequest editFoodRequest,
                                   BindingResult bindingResult) {
        ModelAndView redirect = SessionGuard.redirectToLoginIfNeeded(userSession);
        if (redirect != null) {
            return redirect;
        }

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("food-form");
            modelAndView.addObject("foodId", id);
            modelAndView.addObject("activePage", "foods");
            modelAndView.addObject("isEdit", true);
            return modelAndView;
        }

        foodService.update(userSession.getId(), id, editFoodRequest);
        return new ModelAndView("redirect:/foods");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteFood(@PathVariable UUID id) {
        ModelAndView redirect = SessionGuard.redirectToLoginIfNeeded(userSession);
        if (redirect != null) {
            return redirect;
        }

        foodService.delete(userSession.getId(), id);
        return new ModelAndView("redirect:/foods");
    }
}
