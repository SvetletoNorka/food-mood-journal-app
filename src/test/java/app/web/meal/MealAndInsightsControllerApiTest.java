package app.web.meal;

import app.model.dto.insights.RecommendationDto;
import app.model.dto.insights.RecommendationStatus;
import app.model.dto.meal.MealDetailsDto;
import app.model.dto.statistics.StatisticsPageDto;
import app.model.dto.statistics.StatisticsSort;
import app.model.entity.meal.MealType;
import app.security.AuthenticationMetadata;
import app.service.food.FoodService;
import app.service.insights.InsightsService;
import app.service.meal.MealService;
import app.service.statistics.StatisticsService;
import app.web.insights.InsightsController;
import app.web.statistics.StatisticsController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static app.util.security.TestSecurityUtils.clearAuth;
import static app.util.security.TestSecurityUtils.withAuth;
import static app.util.user.UserFactory.getUserPrincipal;
import static app.util.web.MockMvcTestUtils.standalone;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class MealAndInsightsControllerApiTest {

    @Mock
    private MealService mealService;

    @Mock
    private FoodService foodService;

    @Mock
    private InsightsService insightsService;

    @Mock
    private StatisticsService statisticsService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standalone(
                new MealController(mealService, foodService, insightsService),
                new InsightsController(insightsService),
                new StatisticsController(statisticsService));
    }

    @AfterEach
    void tearDown() {
        clearAuth();
    }

    @Test
    void listMeals_shouldReturnMealsView() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        List<MealDetailsDto> meals = List.of(sampleMeal());
        when(mealService.findAllForUser(principal.getUserId())).thenReturn(meals);

        mockMvc.perform(get("/meals").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("meals"))
                .andExpect(MockMvcResultMatchers.model().attribute("meals", meals));
    }

    @Test
    void newMealForm_shouldReturnMealForm() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        when(foodService.findAllForUser(principal.getUserId())).thenReturn(List.of());

        mockMvc.perform(get("/meals/new").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("meal-form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("createMealRequest"));
    }

    @Test
    void mealDetails_shouldReturnDetailsView() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        MealDetailsDto meal = sampleMeal();
        when(mealService.findById(principal.getUserId(), meal.getId())).thenReturn(meal);

        mockMvc.perform(get("/meals/{id}", meal.getId()).with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("meal-details"))
                .andExpect(MockMvcResultMatchers.model().attribute("meal", meal));
    }

    @Test
    void wellnessForm_shouldDefaultScoresToFive() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        MealDetailsDto meal = sampleMeal();
        when(mealService.findById(principal.getUserId(), meal.getId())).thenReturn(meal);

        mockMvc.perform(get("/meals/{id}/wellness", meal.getId()).with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("meal-wellness"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("wellnessLogRequest"));
    }

    @Test
    void saveWellness_shouldRedirectToInsights() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        MealDetailsDto meal = sampleMeal();
        meal.setMoodScore(8);
        meal.setEnergyScore(7);
        when(mealService.findById(principal.getUserId(), meal.getId())).thenReturn(meal);
        when(insightsService.generateFromMeal(eq(principal.getUserId()), any()))
                .thenReturn(RecommendationDto.builder().title("Eat veggies").build());

        mockMvc.perform(post("/meals/{id}/wellness", meal.getId())
                        .with(withAuth(principal))
                        .param("moodScore", "8")
                        .param("energyScore", "7")
                        .param("notes", "Nice"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/insights"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("successMessage"));
    }

    @Test
    void listInsights_shouldReturnInsightsView() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        List<RecommendationDto> recommendations = List.of(
                RecommendationDto.builder().title("Tip").status(RecommendationStatus.ACTIVE).build());
        when(insightsService.listForUser(principal.getUserId(), null)).thenReturn(recommendations);

        mockMvc.perform(get("/insights").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("insights"))
                .andExpect(MockMvcResultMatchers.model().attribute("recommendations", recommendations));
    }

    @Test
    void applyRecommendation_shouldRedirect() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/insights/{id}/apply", id).with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/insights"));

        verify(insightsService).apply(id);
    }

    @Test
    void dismissRecommendation_shouldRedirect() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/insights/{id}/dismiss", id).with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/insights"));

        verify(insightsService).dismiss(id);
    }

    @Test
    void statistics_shouldReturnStatisticsView() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        StatisticsPageDto page = StatisticsPageDto.builder()
                .allMeals(List.of())
                .topFive(List.of())
                .sort(StatisticsSort.MOOD)
                .averageMood(0)
                .averageEnergy(0)
                .build();
        when(statisticsService.buildPage(principal.getUserId(), StatisticsSort.MOOD)).thenReturn(page);

        mockMvc.perform(get("/statistics").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("statistics"))
                .andExpect(MockMvcResultMatchers.model().attribute("statistics", page))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSort", StatisticsSort.MOOD));
    }

    private MealDetailsDto sampleMeal() {
        return MealDetailsDto.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.BREAKFAST)
                .eatenAt(LocalDateTime.now())
                .foods(List.of())
                .totalCalories(100)
                .totalProtein(10)
                .totalFat(5)
                .totalCarbs(8)
                .build();
    }
}
