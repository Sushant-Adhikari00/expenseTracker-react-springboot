package com.expensetracker.expenseTracker.controller;

import com.expensetracker.expenseTracker.dto.request.ExpenseRequest;
import com.expensetracker.expenseTracker.dto.response.ExpenseResponse;
import com.expensetracker.expenseTracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // POST /api/expenses
    @PostMapping
    public ResponseEntity<ExpenseResponse> create(
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(expenseService.create(request));
    }

    // GET /api/expenses
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAll() {
        return ResponseEntity.ok(expenseService.getAllForCurrentUser());
    }

    // GET /api/expenses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getById(id));
    }

    // PUT /api/expenses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.update(id, request));
    }

    // DELETE /api/expenses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
