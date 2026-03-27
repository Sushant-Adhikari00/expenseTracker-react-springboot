package com.expensetracker.expenseTracker.dto.response;

import com.expensetracker.expenseTracker.entity.Income.IncomeCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class IncomeResponse {
    private Long id;
    private String source;
    private BigDecimal amount;
    private LocalDate date;
    private IncomeCategory category;
    private String note;
    private LocalDateTime createdAt;
}