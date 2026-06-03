package app.web.statistics;

import app.config.UserSession;
import app.model.dto.statistics.StatisticsSort;
import app.service.statistics.StatisticsService;
import app.web.SessionGuard;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final UserSession userSession;

    public StatisticsController(StatisticsService statisticsService, UserSession userSession) {
        this.statisticsService = statisticsService;
        this.userSession = userSession;
    }

    @GetMapping
    public ModelAndView statistics(@RequestParam(defaultValue = "MOOD") StatisticsSort sort) {
        ModelAndView redirect = SessionGuard.redirectToLoginIfNeeded(userSession);
        if (redirect != null) {
            return redirect;
        }

        ModelAndView modelAndView = new ModelAndView("statistics");
        modelAndView.addObject("statistics", statisticsService.buildPage(userSession.getId(), sort));
        modelAndView.addObject("activePage", "statistics");
        modelAndView.addObject("currentSort", sort);
        return modelAndView;
    }
}
