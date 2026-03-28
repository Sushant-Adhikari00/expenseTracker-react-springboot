package com.expensetracker.expenseTracker.service.impl;

import com.expensetracker.expenseTracker.dto.request.IncomeRequest;
import com.expensetracker.expenseTracker.dto.response.IncomeResponse;
import com.expensetracker.expenseTracker.entity.Income;
import com.expensetracker.expenseTracker.entity.User;
import com.expensetracker.expenseTracker.mapper.IncomeMapper;
import com.expensetracker.expenseTracker.repository.IncomeRepository;
import com.expensetracker.expenseTracker.repository.UserRepository;
import com.expensetracker.expenseTracker.security.SecurityUtils;
import com.expensetracker.expenseTracker.service.IncomeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final IncomeMapper incomeMapper;

    @Override
    @Transactional
    public IncomeResponse create(IncomeRequest request) {
        User user = getCurrentUser();

        // Mapper converts request + user → entity
        Income income = incomeMapper.toEntity(request, user);

        // Mapper converts saved entity → response
        return incomeMapper.toResponse(incomeRepository.save(income));
    }

    @Override
    @Transactional(readOnly = true)
    public IncomeResponse getById(Long id) {
        // Mapper converts entity → response
        return incomeMapper.toResponse(findOwnedIncome(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getAllForCurrentUser() {
        Long userId = getCurrentUser().getId();

        // Mapper converts each entity → response
        return incomeRepository
                .findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(incomeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public IncomeResponse update(Long id, IncomeRequest request) {
        Income income = findOwnedIncome(id);

        // Mapper applies non-null request fields onto existing entity
        incomeMapper.updateEntityFromRequest(request, income);

        // Mapper converts updated entity → response
        return incomeMapper.toResponse(incomeRepository.save(income));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        incomeRepository.delete(findOwnedIncome(id));
    }

    // Ownership check — prevents users accessing other users' data
    private Income findOwnedIncome(Long id) {
        Long userId = getCurrentUser().getId();
        return incomeRepository.findById(id)
                .filter(i -> i.getUser().getId().equals(userId))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Income not found with id: " + id));
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
