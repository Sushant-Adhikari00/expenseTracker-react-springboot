package com.expensetracker.expenseTracker.service.impl;

import com.expensetracker.expenseTracker.dto.request.SavingGoalRequest;
import com.expensetracker.expenseTracker.dto.response.SavingGoalResponse;
import com.expensetracker.expenseTracker.entity.SavingGoal;
import com.expensetracker.expenseTracker.entity.User;
import com.expensetracker.expenseTracker.mapper.SavingGoalMapper;
import com.expensetracker.expenseTracker.repository.SavingGoalRepository;
import com.expensetracker.expenseTracker.repository.UserRepository;
import com.expensetracker.expenseTracker.security.SecurityUtils;
import com.expensetracker.expenseTracker.service.SavingGoalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingGoalServiceImpl implements SavingGoalService {

    private final SavingGoalRepository savingGoalRepository;
    private final UserRepository userRepository;
    private final SavingGoalMapper savingGoalMapper;

    @Override
    @Transactional
    public SavingGoalResponse create(SavingGoalRequest request) {
        User user = getCurrentUser();

        // Mapper converts request + user → entity
        SavingGoal goal = savingGoalMapper.toEntity(request, user);

        // Mapper converts saved entity → response
        return savingGoalMapper.toResponse(savingGoalRepository.save(goal));
    }

    @Override
    @Transactional(readOnly = true)
    public SavingGoalResponse getById(Long id) {
        // Mapper converts entity → response
        return savingGoalMapper.toResponse(findOwnedGoal(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingGoalResponse> getAllForCurrentUser() {
        Long userId = getCurrentUser().getId();

        // Mapper converts each entity → response
        return savingGoalRepository
                .findByUserIdOrderByTargetDateAsc(userId)
                .stream()
                .map(savingGoalMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SavingGoalResponse update(Long id, SavingGoalRequest request) {
        SavingGoal goal = findOwnedGoal(id);

        // Mapper applies non-null request fields onto existing entity
        savingGoalMapper.updateEntityFromRequest(request, goal);

        // Mapper converts updated entity → response
        return savingGoalMapper.toResponse(savingGoalRepository.save(goal));
    }

    @Override
    @Transactional
    public SavingGoalResponse updateStatus(Long id, SavingGoal.GoalStatus status) {
        SavingGoal goal = findOwnedGoal(id);
        goal.setStatus(status);

        // Mapper converts updated entity → response
        return savingGoalMapper.toResponse(savingGoalRepository.save(goal));
    }

    @Override
    @Transactional
    public SavingGoalResponse addSavedAmount(Long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        SavingGoal goal = findOwnedGoal(id);
        BigDecimal updated = goal.getSavedAmount().add(amount);

        // Auto-complete when target is reached
        if (updated.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setSavedAmount(goal.getTargetAmount());
            goal.setStatus(SavingGoal.GoalStatus.COMPLETED);
        } else {
            goal.setSavedAmount(updated);
        }

        // Mapper converts updated entity → response
        return savingGoalMapper.toResponse(savingGoalRepository.save(goal));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        savingGoalRepository.delete(findOwnedGoal(id));
    }

    private SavingGoal findOwnedGoal(Long id) {
        Long userId = getCurrentUser().getId();
        return savingGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Saving goal not found with id: " + id));
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
