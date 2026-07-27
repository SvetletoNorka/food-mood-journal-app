package app.web.meal;

import app.model.dto.meal.CreateMealRequest;
import app.model.dto.meal.MealDetailsDto;
import app.model.dto.meal.MealEntryRequest;
import app.model.dto.meal.WellnessLogRequest;
import app.security.AuthenticationUtils;
import app.service.food.FoodService;
import app.service.insights.InsightsService;
import app.service.meal.MealService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/meals")
public class MealController {

    private final MealService mealService;
    private final FoodService foodService;
    private final InsightsService insightsService;

    public MealController(MealService mealService,
                          FoodService foodService,
                          InsightsService insightsService) {
        this.mealService = mealService;
        this.foodService = foodService;
        this.insightsService = insightsService;
    }

    @GetMapping
    public ModelAndView listMeals() {
        UUID userId = AuthenticationUtils.getCurrentUserId();

        ModelAndView modelAndView = new ModelAndView("meals");
        modelAndView.addObject("meals", mealService.findAllForUser(userId));
        modelAndView.addObject("activePage", "meals");
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView newMealForm() {
        UUID userId = AuthenticationUtils.getCurrentUserId();

        ModelAndView modelAndView = new ModelAndView("meal-form");
        modelAndView.addObject("createMealRequest", defaultCreateMealRequest());
        modelAndView.addObject("foods", foodService.findAllForUser(userId));
        modelAndView.addObject("activePage", "meals");
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createMeal(@Valid @ModelAttribute("createMealRequest") CreateMealRequest createMealRequest,
                                   BindingResult bindingResult) {
        UUID userId = AuthenticationUtils.getCurrentUserId();

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("meal-form");
            modelAndView.addObject("createMealRequest", createMealRequest);
            modelAndView.addObject("foods", foodService.findAllForUser(userId));
            modelAndView.addObject("activePage", "meals");
            return modelAndView;
        }

        UUID mealId = mealService.create(userId, createMealRequest).getId();
        return new ModelAndView("redirect:/meals/" + mealId);
    }

    @GetMapping("/{id}")
    public ModelAndView mealDetails(@PathVariable UUID id) {
        UUID userId = AuthenticationUtils.getCurrentUserId();

        ModelAndView modelAndView = new ModelAndView("meal-details");
        modelAndView.addObject("meal", mealService.findById(userId, id));
        modelAndView.addObject("activePage", "meals");
        return modelAndView;
    }

    @GetMapping("/{id}/wellness")
    public ModelAndView wellnessForm(@PathVariable UUID id) {
        UUID userId = AuthenticationUtils.getCurrentUserId();
        var meal = mealService.findById(userId, id);

        WellnessLogRequest wellnessLogRequest = WellnessLogRequest.builder()
                .moodScore(meal.getMoodScore() != null ? meal.getMoodScore() : 5)
                .energyScore(meal.getEnergyScore() != null ? meal.getEnergyScore() : 5)
                .notes(meal.getNotes())
                .build();

        ModelAndView modelAndView = new ModelAndView("meal-wellness");
        modelAndView.addObject("mealId", id);
        modelAndView.addObject("meal", meal);
        modelAndView.addObject("wellnessLogRequest", wellnessLogRequest);
        modelAndView.addObject("activePage", "meals");
        return modelAndView;
    }

    @PostMapping("/{id}/wellness")
    public ModelAndView saveWellness(@PathVariable UUID id,
                                     @Valid @ModelAttribute("wellnessLogRequest") WellnessLogRequest wellnessLogRequest,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        UUID userId = AuthenticationUtils.getCurrentUserId();

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("meal-wellness");
            modelAndView.addObject("mealId", id);
            modelAndView.addObject("meal", mealService.findById(userId, id));
            modelAndView.addObject("activePage", "meals");
            return modelAndView;
        }

        mealService.saveWellnessLog(userId, id, wellnessLogRequest);
        MealDetailsDto meal = mealService.findById(userId, id);
        var recommendation = insightsService.generateFromMeal(userId, meal);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Wellness saved. New insight: " + recommendation.getTitle());
        return new ModelAndView("redirect:/insights");
    }

    private CreateMealRequest defaultCreateMealRequest() {
        return CreateMealRequest.builder()
                .eatenAt(LocalDateTime.now())
                .entries(new ArrayList<>(List.of(MealEntryRequest.builder().quantityInGrams(100).build())))
                .build();
    }
}
