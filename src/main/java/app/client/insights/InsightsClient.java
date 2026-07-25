package app.client.insights;

import app.model.dto.insights.CreateRecommendationRequest;
import app.model.dto.insights.RecommendationDto;
import app.model.dto.insights.RecommendationStatus;
import app.model.dto.insights.UpdateRecommendationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "insights-service", url = "${insights.service.url}")
public interface InsightsClient {

    @PostMapping("/api/v1/users/{userId}/recommendations")
    RecommendationDto createRecommendation(
            @PathVariable("userId") UUID userId,
            @RequestBody CreateRecommendationRequest request);

    @GetMapping("/api/v1/users/{userId}/recommendations")
    List<RecommendationDto> getRecommendations(
            @PathVariable("userId") UUID userId,
            @RequestParam(value = "status", required = false) RecommendationStatus status);

    @PutMapping("/api/v1/recommendations/{id}")
    RecommendationDto updateRecommendation(
            @PathVariable("id") UUID id,
            @RequestBody UpdateRecommendationRequest request);
}
