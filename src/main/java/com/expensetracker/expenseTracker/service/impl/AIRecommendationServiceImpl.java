package com.expensetracker.expenseTracker.service.impl;

import com.expensetracker.expenseTracker.dto.response.AIRecommendationResponse;
import com.expensetracker.expenseTracker.entity.SavingGoal;
import com.expensetracker.expenseTracker.mapper.AIRecommendationMapper;
import com.expensetracker.expenseTracker.repository.ExpenseRepository;
import com.expensetracker.expenseTracker.repository.IncomeRepository;
import com.expensetracker.expenseTracker.repository.SavingGoalRepository;
import com.expensetracker.expenseTracker.repository.UserRepository;
import com.expensetracker.expenseTracker.security.SecurityUtils;
import com.expensetracker.expenseTracker.service.AIRecommendationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIRecommendationServiceImpl implements AIRecommendationService {

    private final IncomeRepository       incomeRepository;
    private final ExpenseRepository      expenseRepository;
    private final SavingGoalRepository   savingGoalRepository;
    private final UserRepository         userRepository;
    private final AIRecommendationMapper aiRecommendationMapper;

    @Override
    @Transactional(readOnly = true)
    public AIRecommendationResponse getRecommendations() {
        Long userId = getCurrentUserId();

        LocalDate now   = LocalDate.now();
        int       month = now.getMonthValue();
        int       year  = now.getYear();

        BigDecimal totalIncome = incomeRepository
                .sumByUserIdAndMonthAndYear(userId, month, year);

        BigDecimal totalExpenses = expenseRepository
                .sumByUserIdAndMonthAndYear(userId, month, year);

        BigDecimal netSavings = totalIncome.subtract(totalExpenses);

        List<Object[]> expenseCategories = expenseRepository
                .sumByCategoryForMonth(userId, month, year);

        List<SavingGoal> activeGoals = savingGoalRepository
                .findByUserIdAndStatus(userId, SavingGoal.GoalStatus.IN_PROGRESS);

        int healthScore = aiRecommendationMapper
                .toHealthScore(totalIncome, totalExpenses, netSavings);

        return aiRecommendationMapper.toResponse(
                healthScore,
                totalIncome,
                totalExpenses,
                netSavings,
                expenseCategories,
                activeGoals
        );
    }

    private Long getCurrentUserId() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .getId();
    }
}