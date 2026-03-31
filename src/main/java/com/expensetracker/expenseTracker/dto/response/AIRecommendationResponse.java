package com.expensetracker.expenseTracker.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AIRecommendationResponse {

    private String              overallHealth;      // EXCELLENT, GOOD, WARNING, CRITICAL
    private int                 healthScore;        // 0-100
    private List<Recommendation> recommendations;
    private SavingsPlan          savingsPlan;
    private List<GoalProjection> goalProjections;

    @Data
    @Builder
    public static class Recommendation {
        private String type;       // SPENDING, SAVING, GOAL, INCOME
        private String severity;   // INFO, WARNING, DANGER, SUCCESS
        private String title;
        private String message;
        private String action;     // actionable step
    }

    @Data
    @Builder
    public static class SavingsPlan {
        private BigDecimal currentMonthlySavings;
        private BigDecimal recommendedMonthlySavings;
        private BigDecimal potentialAnnualSavings;
        private double     currentSavingsRate;
        private double     targetSavingsRate;
    }

    @Data
    @Builder
    public static class GoalProjection {
        private String     goalName;
        private BigDecimal targetAmount;
        private BigDecimal savedAmount;
        private BigDecimal remaining;
        private double     progressPercentage;
        private int        estimatedMonthsToComplete;
        private String     estimatedCompletionDate;
        private String     status;
    }
}