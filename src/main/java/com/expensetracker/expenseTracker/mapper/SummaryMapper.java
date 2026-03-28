package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.response.MonthlySummaryResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SummaryMapper {

    default MonthlySummaryResponse toResponse(
            int month,
            int year,
            BigDecimal totalIncome,
            BigDecimal totalExpenses,
            List<Object[]> incomeCategoryRows,
            List<Object[]> expenseCategoryRows) {

        BigDecimal netSavings = totalIncome.subtract(totalExpenses);
        double savingsRate    = calculateSavingsRate(totalIncome, netSavings);

        return MonthlySummaryResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netSavings(netSavings)
                .savingsRate(savingsRate)
                .incomeByCategory(toCategoryMap(incomeCategoryRows))
                .expenseByCategory(toCategoryMap(expenseCategoryRows))
                .expensesExceed70Percent(isExpensesOver70Percent(totalIncome, totalExpenses))
                .savingsRateLow(savingsRate < 20.0)
                .build();
    }

    default Map<String, BigDecimal> toCategoryMap(List<Object[]> rows) {
        return rows.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (BigDecimal) row[1]
                ));
    }

    default double calculateSavingsRate(BigDecimal income, BigDecimal savings) {
        if (income.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return savings
                .divide(income, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    default boolean isExpensesOver70Percent(BigDecimal income, BigDecimal expenses) {
        if (income.compareTo(BigDecimal.ZERO) == 0) return false;
        return expenses
                .divide(income, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .compareTo(BigDecimal.valueOf(70)) > 0;
    }
}
