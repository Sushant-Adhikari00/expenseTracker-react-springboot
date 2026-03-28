package com.expensetracker.expenseTracker.controller;

import com.expensetracker.expenseTracker.dto.response.MonthlySummaryResponse;
import com.expensetracker.expenseTracker.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    // GET /api/summary/monthly
    // GET /api/summary/monthly?month=6&year=2025
    @GetMapping("/monthly")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {

        // Default to current month and year if not provided
        LocalDate now = LocalDate.now();
        int resolvedMonth = month == 0 ? now.getMonthValue() : month;
        int resolvedYear  = year  == 0 ? now.getYear()       : year;

        return ResponseEntity.ok(
                summaryService.getMonthlySummary(resolvedMonth, resolvedYear));
    }
}