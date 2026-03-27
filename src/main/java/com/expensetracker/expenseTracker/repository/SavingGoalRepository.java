package com.expensetracker.expenseTracker.repository;

import com.expensetracker.expenseTracker.entity.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {

    List<SavingGoal> findByUserIdOrderByTargetDateAsc(Long userId);

    List<SavingGoal> findByUserIdAndStatus(Long userId, SavingGoal.GoalStatus status);

    Optional<SavingGoal> findByIdAndUserId(Long id, Long userId);
}