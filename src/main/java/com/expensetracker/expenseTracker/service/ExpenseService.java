package com.expensetracker.expenseTracker.service;

import com.expensetracker.expenseTracker.dto.request.ExpenseRequest;
import com.expensetracker.expenseTracker.dto.response.ExpenseResponse;

import java.util.List;

public interface ExpenseService {
    ExpenseResponse create(ExpenseRequest request);
    ExpenseResponse getById(Long id);
    List<ExpenseResponse> getAllForCurrentUser();
    ExpenseResponse update(Long id, ExpenseRequest request);
    void delete(Long id);
}