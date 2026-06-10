package app.web.statistics;

import app.model.dto.statistics.StatisticsSort;
import app.service.statistics.StatisticsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ModelAndView statistics(@RequestParam(defaultValue = "MOOD") StatisticsSort sort,
                                   HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");

        ModelAndView modelAndView = new ModelAndView("statistics");
        modelAndView.addObject("statistics", statisticsService.buildPage(userId, sort));
        modelAndView.addObject("activePage", "statistics");
        modelAndView.addObject("currentSort", sort);
        return modelAndView;
    }
}
