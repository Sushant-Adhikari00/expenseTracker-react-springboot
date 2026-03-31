package com.expensetracker.expenseTracker.controller;

import com.expensetracker.expenseTracker.dto.response.AIRecommendationResponse;
import com.expensetracker.expenseTracker.service.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIRecommendationController {

    private final AIRecommendationService aiRecommendationService;

    @GetMapping("/recommendations")
    public ResponseEntity<AIRecommendationResponse> getRecommendations() {
        return ResponseEntity.ok(aiRecommendationService.getRecommendations());
    }
}
