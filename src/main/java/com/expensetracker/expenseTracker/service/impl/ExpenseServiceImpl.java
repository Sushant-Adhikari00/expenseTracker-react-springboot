package com.expensetracker.expenseTracker.service.impl;

import com.expensetracker.expenseTracker.dto.request.ExpenseRequest;
import com.expensetracker.expenseTracker.dto.response.ExpenseResponse;
import com.expensetracker.expenseTracker.dto.response.PageResponse;
import com.expensetracker.expenseTracker.entity.Expense;
import com.expensetracker.expenseTracker.entity.User;
import com.expensetracker.expenseTracker.mapper.ExpenseMapper;
import com.expensetracker.expenseTracker.repository.ExpenseRepository;
import com.expensetracker.expenseTracker.repository.UserRepository;
import com.expensetracker.expenseTracker.security.SecurityUtils;
import com.expensetracker.expenseTracker.service.ExpenseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        User user = getCurrentUser();

        // Mapper converts request + user → entity
        Expense expense = expenseMapper.toEntity(request, user);

        // Mapper converts saved entity → response
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getById(Long id) {
        // Mapper converts entity → response
        return expenseMapper.toResponse(findOwnedExpense(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExpenseResponse> getAllForCurrentUser(Pageable pageable) {
        Long userId = getCurrentUser().getId();

        // Mapper converts Page<Expense> → PageResponse<ExpenseResponse>
        return expenseMapper.toPageResponse(
                expenseRepository.findByUserIdOrderByDateDesc(userId, pageable));
    }

    @Override
    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = findOwnedExpense(id);

        // Mapper applies non-null request fields onto existing entity
        expenseMapper.updateEntityFromRequest(request, expense);

        // Mapper converts updated entity → response
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        expenseRepository.delete(findOwnedExpense(id));
    }

    private Expense findOwnedExpense(Long id) {
        Long userId = getCurrentUser().getId();
        return expenseRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(userId))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Expense not found with id: " + id));
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
