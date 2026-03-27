package com.expensetracker.expenseTracker.dto.response;

import com.expensetracker.expenseTracker.entity.Expense.ExpenseCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseResponse {
    private Long id;
    private String title;
    private BigDecimal amount;
    private LocalDate date;
    private ExpenseCategory category;
    private String note;
    private LocalDateTime createdAt;
}