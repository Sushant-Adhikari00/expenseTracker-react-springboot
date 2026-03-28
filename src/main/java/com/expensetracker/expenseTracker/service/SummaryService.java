package com.expensetracker.expenseTracker.service;

import com.expensetracker.expenseTracker.dto.response.MonthlySummaryResponse;

public interface SummaryService {
    MonthlySummaryResponse getMonthlySummary(int month, int year);
}
