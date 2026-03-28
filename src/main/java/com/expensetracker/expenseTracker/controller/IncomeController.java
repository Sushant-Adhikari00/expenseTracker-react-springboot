package com.expensetracker.expenseTracker.controller;

import com.expensetracker.expenseTracker.dto.request.IncomeRequest;
import com.expensetracker.expenseTracker.dto.response.IncomeResponse;
import com.expensetracker.expenseTracker.service.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    // POST /api/incomes
    @PostMapping
    public ResponseEntity<IncomeResponse> create(
            @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(incomeService.create(request));
    }

    // GET /api/incomes
    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getAll() {
        return ResponseEntity.ok(incomeService.getAllForCurrentUser());
    }

    // GET /api/incomes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(incomeService.getById(id));
    }

    // PUT /api/incomes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(incomeService.update(id, request));
    }

    // DELETE /api/incomes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        incomeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}