package com.expensetracker.expenseTracker.service;

import com.expensetracker.expenseTracker.dto.request.IncomeRequest;
import com.expensetracker.expenseTracker.dto.response.IncomeResponse;


import java.util.List;

public interface IncomeService {
    IncomeResponse create(IncomeRequest request);
    IncomeResponse getById(Long id);
    List<IncomeResponse> getAllForCurrentUser();
    IncomeResponse update(Long id, IncomeRequest request);
    void delete(Long id);
}
