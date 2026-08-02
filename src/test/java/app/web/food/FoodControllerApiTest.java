package app.web.food;

import app.model.dto.food.FoodDto;
import app.security.AuthenticationMetadata;
import app.service.food.FoodService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
class FoodControllerApiTest {

    @Mock
    private FoodService foodService;

    @InjectMocks
    private FoodController foodController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standalone(foodController);
    }

    @AfterEach
    void tearDown() {
        clearAuth();
    }

    @Test
    void listFoods_shouldReturnFoodsView() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        List<FoodDto> foods = List.of(FoodDto.builder()
                .id(UUID.randomUUID())
                .name("Apple")
                .caloriesPer100g(52)
                .build());
        when(foodService.findAllForUser(principal.getUserId())).thenReturn(foods);

        mockMvc.perform(get("/foods").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("foods"))
                .andExpect(MockMvcResultMatchers.model().attribute("foods", foods))
                .andExpect(MockMvcResultMatchers.model().attribute("activePage", "foods"));
    }

    @Test
    void newFoodForm_shouldReturnFoodForm() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        mockMvc.perform(get("/foods/new").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("food-form"))
                .andExpect(MockMvcResultMatchers.model().attribute("isEdit", false));
    }

    @Test
    void createFood_shouldRedirectToFoods() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();

        mockMvc.perform(post("/foods")
                        .with(withAuth(principal))
                        .param("name", "Rice")
                        .param("caloriesPer100g", "130")
                        .param("proteinPer100g", "2.7")
                        .param("fatPer100g", "0.3")
                        .param("carbsPer100g", "28"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/foods"));

        verify(foodService).create(eq(principal.getUserId()), any());
    }

    @Test
    void editFoodForm_shouldPopulateEditModel() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        UUID foodId = UUID.randomUUID();
        when(foodService.findById(principal.getUserId(), foodId)).thenReturn(FoodDto.builder()
                .id(foodId)
                .name("Banana")
                .caloriesPer100g(89)
                .proteinPer100g(1)
                .fatPer100g(0.3)
                .carbsPer100g(23)
                .build());

        mockMvc.perform(get("/foods/{id}/edit", foodId).with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("food-form"))
                .andExpect(MockMvcResultMatchers.model().attribute("isEdit", true))
                .andExpect(MockMvcResultMatchers.model().attribute("foodId", foodId));
    }

    @Test
    void deleteFood_shouldRedirectToFoods() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        UUID foodId = UUID.randomUUID();

        mockMvc.perform(post("/foods/{id}/delete", foodId).with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/foods"));

        verify(foodService).delete(principal.getUserId(), foodId);
    }
}
