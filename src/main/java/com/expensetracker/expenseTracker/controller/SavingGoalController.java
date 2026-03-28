package com.expensetracker.expenseTracker.controller;

import com.expensetracker.expenseTracker.dto.request.SavingGoalRequest;
import com.expensetracker.expenseTracker.dto.response.SavingGoalResponse;
import com.expensetracker.expenseTracker.entity.SavingGoal;
import com.expensetracker.expenseTracker.service.SavingGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class SavingGoalController {

    private final SavingGoalService savingGoalService;

    // POST /api/goals
    @PostMapping
    public ResponseEntity<SavingGoalResponse> create(
            @Valid @RequestBody SavingGoalRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savingGoalService.create(request));
    }

    // GET /api/goals
    @GetMapping
    public ResponseEntity<List<SavingGoalResponse>> getAll() {
        return ResponseEntity.ok(savingGoalService.getAllForCurrentUser());
    }

    // GET /api/goals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SavingGoalResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(savingGoalService.getById(id));
    }

    // PUT /api/goals/{id}
    @PutMapping("/{id}")
    public ResponseEntity<SavingGoalResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SavingGoalRequest request) {
        return ResponseEntity.ok(savingGoalService.update(id, request));
    }

    // PATCH /api/goals/{id}/deposit?amount=500
    @PatchMapping("/{id}/deposit")
    public ResponseEntity<SavingGoalResponse> deposit(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(savingGoalService.addSavedAmount(id, amount));
    }

    // PATCH /api/goals/{id}/status?status=CANCELLED
    @PatchMapping("/{id}/status")
    public ResponseEntity<SavingGoalResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam SavingGoal.GoalStatus status) {
        return ResponseEntity.ok(savingGoalService.updateStatus(id, status));
    }

    // DELETE /api/goals/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        savingGoalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}