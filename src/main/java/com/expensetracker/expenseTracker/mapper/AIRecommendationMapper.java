package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.response.AIRecommendationResponse;
import com.expensetracker.expenseTracker.entity.SavingGoal;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AIRecommendationMapper {

    // ── Full Response Assembly ───────────────────────────────────────────────

    default AIRecommendationResponse toResponse(
            int              healthScore,
            BigDecimal       totalIncome,
            BigDecimal       totalExpenses,
            BigDecimal       netSavings,
            List<Object[]>   expenseCategories,
            List<SavingGoal> activeGoals) {

        return AIRecommendationResponse.builder()
                .overallHealth(toHealthLabel(healthScore))
                .healthScore(healthScore)
                .recommendations(toRecommendations(
                        totalIncome, totalExpenses,
                        netSavings, expenseCategories, activeGoals))
                .savingsPlan(toSavingsPlan(totalIncome, netSavings))
                .goalProjections(toGoalProjections(activeGoals, netSavings))
                .build();
    }

    // ── Health Score ─────────────────────────────────────────────────────────

    default int toHealthScore(
            BigDecimal income,
            BigDecimal expenses,
            BigDecimal savings) {

        if (income.compareTo(BigDecimal.ZERO) == 0) return 0;

        int    score       = 50;
        double savingsRate  = toPercent(savings,  income);
        double expenseRatio = toPercent(expenses, income);

        // Savings rate scoring
        if      (savingsRate >= 30) score += 30;
        else if (savingsRate >= 20) score += 20;
        else if (savingsRate >= 10) score += 10;
        else if (savingsRate <   0) score -= 30;

        // Expense ratio scoring
        if      (expenseRatio <= 50) score += 20;
        else if (expenseRatio <= 70) score += 10;
        else if (expenseRatio <= 90) score -= 10;
        else                         score -= 20;

        return Math.max(0, Math.min(100, score));
    }

    default String toHealthLabel(int score) {
        if (score >= 80) return "EXCELLENT";
        if (score >= 60) return "GOOD";
        if (score >= 40) return "WARNING";
        return "CRITICAL";
    }

    // ── Recommendations ──────────────────────────────────────────────────────

    default List<AIRecommendationResponse.Recommendation> toRecommendations(
            BigDecimal       income,
            BigDecimal       expenses,
            BigDecimal       savings,
            List<Object[]>   expenseCategories,
            List<SavingGoal> goals) {

        List<AIRecommendationResponse.Recommendation> list = new ArrayList<>();

        // No income recorded
        if (income.compareTo(BigDecimal.ZERO) == 0) {
            list.add(toRecommendation(
                    "INCOME", "WARNING",
                    "No income recorded this month",
                    "You have no income recorded. Add your income sources to get accurate recommendations.",
                    "Go to Income page and add your monthly income"
            ));
            return list;
        }

        double savingsRate  = toPercent(savings,  income);
        double expenseRatio = toPercent(expenses, income);

        // Rule 1 — Spending > 90%
        if (expenseRatio > 90) {
            list.add(toRecommendation(
                    "SPENDING", "DANGER",
                    "Critical: Spending nearly all income",
                    String.format(
                            "You are spending %.1f%% of your income. " +
                                    "This leaves almost nothing for savings or emergencies.",
                            expenseRatio),
                    "Identify your top 3 expense categories and cut each by 10%"
            ));
        }
        // Rule 2 — Spending > 70%
        else if (expenseRatio > 70) {
            list.add(toRecommendation(
                    "SPENDING", "WARNING",
                    "High spending detected",
                    String.format(
                            "Your expenses are %.1f%% of your income. " +
                                    "Experts recommend keeping expenses under 70%%.",
                            expenseRatio),
                    "Review discretionary spending and aim to reduce by 5-10%"
            ));
        }

        // Rule 3 — Negative savings
        if (savings.compareTo(BigDecimal.ZERO) < 0) {
            list.add(toRecommendation(
                    "SPENDING", "DANGER",
                    "Spending exceeds income",
                    String.format(
                            "You have spent $%.2f more than you earned this month. " +
                                    "This is unsustainable.",
                            savings.abs().doubleValue()),
                    "Immediately cut non-essential expenses and review all subscriptions"
            ));
        }

        // Rule 4 — Low savings rate
        if (savingsRate >= 0 && savingsRate < 10) {
            list.add(toRecommendation(
                    "SAVING", "WARNING",
                    "Low savings rate",
                    String.format(
                            "You are saving only %.1f%% of income. " +
                                    "The recommended minimum is 20%%.",
                            savingsRate),
                    "Try the 50/30/20 rule: 50% needs, 30% wants, 20% savings"
            ));
        }

        // Rule 5 — Excellent savings rate >= 20%
        if (savingsRate >= 20) {
            list.add(toRecommendation(
                    "SAVING", "SUCCESS",
                    "Excellent savings rate!",
                    String.format(
                            "You are saving %.1f%% of your income. " +
                                    "You are on track for financial independence.",
                            savingsRate),
                    "Consider investing your surplus in index funds or ETFs"
            ));
        }

        // Rule 6 — Outstanding financial health
        if (savingsRate >= 30 && expenseRatio <= 60) {
            list.add(toRecommendation(
                    "INCOME", "SUCCESS",
                    "Outstanding financial discipline",
                    "You are in the top tier of financial health. " +
                            "Your habits will compound significantly over time.",
                    "Consider maxing out tax-advantaged retirement accounts"
            ));
        }

        // Rule 7 — Top expense category > 40% of total expenses
        expenseCategories.stream()
                .filter(row -> {
                    BigDecimal amount = (BigDecimal) row[1];
                    return expenses.compareTo(BigDecimal.ZERO) > 0
                            && toPercent(amount, expenses) > 40;
                })
                .findFirst()
                .ifPresent(row -> {
                    String     cat    = row[0].toString();
                    BigDecimal amount = (BigDecimal) row[1];
                    list.add(toRecommendation(
                            "SPENDING", "WARNING",
                            "High spending in " + cat,
                            String.format(
                                    "%s accounts for %.1f%% of your total expenses ($%.2f). " +
                                            "Consider reducing this category.",
                                    cat,
                                    toPercent(amount, expenses),
                                    amount.doubleValue()),
                            "Set a monthly budget limit for " + cat
                    ));
                });

        // Rule 8 — No active saving goals
        if (goals.isEmpty()) {
            list.add(toRecommendation(
                    "GOAL", "INFO",
                    "No active saving goals",
                    "Setting clear saving goals increases your likelihood " +
                            "of financial success by 42%.",
                    "Create an emergency fund covering 3-6 months of expenses"
            ));
        }

        // Rule 9 — Goal behind schedule
        goals.forEach(goal -> {
            double progress  = goal.getProgressPercentage();
            long   daysLeft  = java.time.temporal.ChronoUnit.DAYS
                    .between(LocalDate.now(), goal.getTargetDate());
            long   totalDays = java.time.temporal.ChronoUnit.DAYS
                    .between(goal.getCreatedAt().toLocalDate(), goal.getTargetDate());
            double expected  = totalDays > 0
                    ? ((double)(totalDays - daysLeft) / totalDays) * 100
                    : 0;

            if (progress < expected - 10 && daysLeft > 0) {
                list.add(toRecommendation(
                        "GOAL", "WARNING",
                        "Goal behind schedule: " + goal.getName(),
                        String.format(
                                "You are %.1f%% complete but should be at %.1f%% by now. " +
                                        "%d days remaining.",
                                progress, expected, daysLeft),
                        "Increase your monthly contribution to stay on track"
                ));
            }
        });

        return list;
    }

    // ── Single Recommendation Builder ────────────────────────────────────────

    default AIRecommendationResponse.Recommendation toRecommendation(
            String type,
            String severity,
            String title,
            String message,
            String action) {

        return AIRecommendationResponse.Recommendation.builder()
                .type(type)
                .severity(severity)
                .title(title)
                .message(message)
                .action(action)
                .build();
    }

    // ── Savings Plan ─────────────────────────────────────────────────────────

    default AIRecommendationResponse.SavingsPlan toSavingsPlan(
            BigDecimal income,
            BigDecimal currentSavings) {

        double currentRate = income.compareTo(BigDecimal.ZERO) == 0
                ? 0
                : toPercent(currentSavings, income);

        double targetRate = 20.0;
        if (currentRate >= 20) targetRate = 30.0;
        if (currentRate >= 30) targetRate = 40.0;

        BigDecimal recommended = income
                .multiply(BigDecimal.valueOf(targetRate / 100))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal annualPotential = recommended
                .multiply(BigDecimal.valueOf(12))
                .setScale(2, RoundingMode.HALF_UP);

        return AIRecommendationResponse.SavingsPlan.builder()
                .currentMonthlySavings(currentSavings.max(BigDecimal.ZERO))
                .recommendedMonthlySavings(recommended)
                .potentialAnnualSavings(annualPotential)
                .currentSavingsRate(Math.max(currentRate, 0))
                .targetSavingsRate(targetRate)
                .build();
    }

    // ── Goal Projections ─────────────────────────────────────────────────────

    default List<AIRecommendationResponse.GoalProjection> toGoalProjections(
            List<SavingGoal> goals,
            BigDecimal       monthlySavings) {

        List<AIRecommendationResponse.GoalProjection> projections = new ArrayList<>();

        if (goals.isEmpty()) return projections;

        int goalCount = goals.size();

        BigDecimal perGoal = monthlySavings.compareTo(BigDecimal.ZERO) > 0
                ? monthlySavings.divide(
                BigDecimal.valueOf(goalCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        for (SavingGoal goal : goals) {
            projections.add(toGoalProjection(goal, perGoal));
        }

        return projections;
    }

    default AIRecommendationResponse.GoalProjection toGoalProjection(
            SavingGoal goal,
            BigDecimal monthlyContribution) {

        BigDecimal remaining = goal.getTargetAmount()
                .subtract(goal.getSavedAmount())
                .max(BigDecimal.ZERO);

        int    estimatedMonths = 0;
        String completionDate  = "Unknown";

        if (monthlyContribution.compareTo(BigDecimal.ZERO) > 0
                && remaining.compareTo(BigDecimal.ZERO) > 0) {
            estimatedMonths = remaining
                    .divide(monthlyContribution, 0, RoundingMode.CEILING)
                    .intValue();
            completionDate = LocalDate.now()
                    .plusMonths(estimatedMonths)
                    .format(DateTimeFormatter.ofPattern("MMM yyyy"));
        } else if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            estimatedMonths = 0;
            completionDate  = "Goal met!";
        }

        return AIRecommendationResponse.GoalProjection.builder()
                .goalName(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .savedAmount(goal.getSavedAmount())
                .remaining(remaining)
                .progressPercentage(goal.getProgressPercentage())
                .estimatedMonthsToComplete(estimatedMonths)
                .estimatedCompletionDate(completionDate)
                .status(goal.getStatus().name())
                .build();
    }

    // ── Utility ──────────────────────────────────────────────────────────────

    default double toPercent(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return 0;
        return part
                .divide(total, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}