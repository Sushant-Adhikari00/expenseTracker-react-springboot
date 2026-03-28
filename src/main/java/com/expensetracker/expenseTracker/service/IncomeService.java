package com.expensetracker.expenseTracker.service;

import com.expensetracker.expenseTracker.dto.request.IncomeRequest;
import com.expensetracker.expenseTracker.dto.response.IncomeResponse;
import com.expensetracker.expenseTracker.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface IncomeService {
    IncomeResponse create(IncomeRequest request);
    IncomeResponse getById(Long id);
    PageResponse<IncomeResponse> getAllForCurrentUser(Pageable pageable);
    IncomeResponse update(Long id, IncomeRequest request);
    void delete(Long id);
}
