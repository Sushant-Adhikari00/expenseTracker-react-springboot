package com.expensetracker.expenseTracker.service.impl;

import com.expensetracker.expenseTracker.dto.response.MonthlySummaryResponse;
import com.expensetracker.expenseTracker.mapper.SummaryMapper;
import com.expensetracker.expenseTracker.repository.ExpenseRepository;
import com.expensetracker.expenseTracker.repository.IncomeRepository;
import com.expensetracker.expenseTracker.repository.UserRepository;
import com.expensetracker.expenseTracker.security.SecurityUtils;
import com.expensetracker.expenseTracker.service.SummaryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final SummaryMapper summaryMapper;

    @Override
    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(int month, int year) {
        Long userId = getCurrentUserId();

        BigDecimal totalIncome = incomeRepository
                .sumByUserIdAndMonthAndYear(userId, month, year);

        BigDecimal totalExpenses = expenseRepository
                .sumByUserIdAndMonthAndYear(userId, month, year);

        // Mapper handles all computation and response assembly
        return summaryMapper.toResponse(
                month,
                year,
                totalIncome,
                totalExpenses,
                incomeRepository.sumByCategoryForMonth(userId, month, year),
                expenseRepository.sumByCategoryForMonth(userId, month, year)
        );
    }

    private Long getCurrentUserId() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .getId();
    }
}
