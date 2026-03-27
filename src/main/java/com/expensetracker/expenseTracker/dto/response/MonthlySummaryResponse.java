package com.expensetracker.expenseTracker.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class MonthlySummaryResponse {

    private int month;
    private int year;

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netSavings;          // income - expenses
    private double savingsRate;             // (netSavings / income) * 100

    // Category breakdowns for charts
    private Map<String, BigDecimal> incomeByCategory;
    private Map<String, BigDecimal> expenseByCategory;

    // AI suggestion flag inputs (used in Phase 5)
    private boolean expensesExceed70Percent;
    private boolean savingsRateLow;
}
