package com.expensetracker.expenseTracker.dto.response;

import com.expensetracker.expenseTracker.entity.SavingGoal.GoalStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SavingGoalResponse {
    private Long id;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal savedAmount;
    private double progressPercentage;
    private LocalDate targetDate;
    private GoalStatus status;
    private String note;
    private LocalDateTime createdAt;
}