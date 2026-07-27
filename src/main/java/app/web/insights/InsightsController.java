package app.web.insights;

import app.model.dto.insights.RecommendationStatus;
import app.security.AuthenticationUtils;
import app.service.insights.InsightsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/insights")
@RequiredArgsConstructor
public class InsightsController {

    private final InsightsService insightsService;

    @GetMapping
    public ModelAndView listInsights(@RequestParam(required = false) RecommendationStatus status) {
        UUID userId = AuthenticationUtils.getCurrentUserId();

        ModelAndView modelAndView = new ModelAndView("insights");
        modelAndView.addObject("recommendations", insightsService.listForUser(userId, status));
        modelAndView.addObject("currentStatus", status);
        modelAndView.addObject("activePage", "insights");
        return modelAndView;
    }

    @PostMapping("/{id}/apply")
    public ModelAndView applyRecommendation(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        insightsService.apply(id);
        redirectAttributes.addFlashAttribute("successMessage", "Recommendation marked as applied.");
        return new ModelAndView("redirect:/insights");
    }

    @PostMapping("/{id}/dismiss")
    public ModelAndView dismissRecommendation(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        insightsService.dismiss(id);
        redirectAttributes.addFlashAttribute("successMessage", "Recommendation dismissed.");
        return new ModelAndView("redirect:/insights");
    }
}
