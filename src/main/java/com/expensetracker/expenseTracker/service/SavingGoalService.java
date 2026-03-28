package com.expensetracker.expenseTracker.service;

import com.expensetracker.expenseTracker.dto.request.SavingGoalRequest;
import com.expensetracker.expenseTracker.dto.response.SavingGoalResponse;
import com.expensetracker.expenseTracker.entity.SavingGoal;

import java.math.BigDecimal;
import java.util.List;

public interface SavingGoalService {
    SavingGoalResponse create(SavingGoalRequest request);
    SavingGoalResponse getById(Long id);
    List<SavingGoalResponse> getAllForCurrentUser();
    SavingGoalResponse update(Long id, SavingGoalRequest request);
    SavingGoalResponse updateStatus(Long id, SavingGoal.GoalStatus status);
    SavingGoalResponse addSavedAmount(Long id, BigDecimal amount);
    void delete(Long id);
}
