package app.service.statistics;

import app.model.dto.meal.MealDetailsDto;
import app.model.dto.statistics.StatisticsPageDto;
import app.model.dto.statistics.StatisticsSort;
import app.model.entity.meal.MealType;
import app.service.meal.MealService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceUnitTest {

    @Mock
    private MealService mealService;

    @InjectMocks
    private StatisticsService underTest;

    @Test
    void buildPage_shouldComputeAveragesAndTopFiveByMood() {
        UUID ownerId = UUID.randomUUID();
        List<MealDetailsDto> meals = List.of(
                meal(9, 5, 10, 10, 10),
                meal(3, 8, 20, 5, 15),
                meal(null, null, 30, 30, 30)
        );
        when(mealService.findAllForUser(ownerId)).thenReturn(meals);

        StatisticsPageDto page = underTest.buildPage(ownerId, StatisticsSort.MOOD);

        assertEquals(3, page.getAllMeals().size());
        assertEquals(StatisticsSort.MOOD, page.getSort());
        assertEquals(6.0, page.getAverageMood());
        assertEquals(6.5, page.getAverageEnergy());
        assertEquals(9, page.getTopFive().get(0).getMoodScore());
    }

    @Test
    void buildPage_shouldSortByProtein() {
        UUID ownerId = UUID.randomUUID();
        MealDetailsDto low = meal(5, 5, 10, 1, 1);
        MealDetailsDto high = meal(5, 5, 50, 1, 1);
        when(mealService.findAllForUser(ownerId)).thenReturn(List.of(low, high));

        StatisticsPageDto page = underTest.buildPage(ownerId, StatisticsSort.PROTEIN);

        assertEquals(50, page.getTopFive().get(0).getTotalProtein());
    }

    @Test
    void buildPage_shouldReturnZeroAverages_whenNoScores() {
        UUID ownerId = UUID.randomUUID();
        when(mealService.findAllForUser(ownerId)).thenReturn(List.of());

        StatisticsPageDto page = underTest.buildPage(ownerId, StatisticsSort.ENERGY);

        assertEquals(0, page.getAverageMood());
        assertEquals(0, page.getAverageEnergy());
        assertTrue(page.getTopFive().isEmpty());
    }

    @Test
    void buildPage_shouldSortByFatAndCarbs() {
        UUID ownerId = UUID.randomUUID();
        MealDetailsDto a = meal(1, 1, 1, 40, 5);
        MealDetailsDto b = meal(1, 1, 1, 10, 50);
        when(mealService.findAllForUser(ownerId)).thenReturn(List.of(a, b));

        assertEquals(40, underTest.buildPage(ownerId, StatisticsSort.FAT).getTopFive().get(0).getTotalFat());
        assertEquals(50, underTest.buildPage(ownerId, StatisticsSort.CARBS).getTopFive().get(0).getTotalCarbs());
    }

    private MealDetailsDto meal(Integer mood, Integer energy, double protein, double fat, double carbs) {
        return MealDetailsDto.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.LUNCH)
                .eatenAt(LocalDateTime.now())
                .moodScore(mood)
                .energyScore(energy)
                .totalProtein(protein)
                .totalFat(fat)
                .totalCarbs(carbs)
                .totalCalories(protein * 4 + fat * 9 + carbs * 4)
                .build();
    }
}
