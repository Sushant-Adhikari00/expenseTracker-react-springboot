package com.expensetracker.expenseTracker.service;

import com.expensetracker.expenseTracker.dto.request.ExpenseRequest;
import com.expensetracker.expenseTracker.dto.response.ExpenseResponse;
import com.expensetracker.expenseTracker.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExpenseService {
    ExpenseResponse create(ExpenseRequest request);
    ExpenseResponse getById(Long id);
    PageResponse<ExpenseResponse> getAllForCurrentUser(Pageable pageable);
    ExpenseResponse update(Long id, ExpenseRequest request);
    void delete(Long id);
}