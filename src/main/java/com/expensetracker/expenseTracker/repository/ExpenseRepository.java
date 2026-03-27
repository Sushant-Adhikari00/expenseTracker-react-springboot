package com.expensetracker.expenseTracker.repository;

import com.expensetracker.expenseTracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    // Sum of expenses for a given month/year
    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
              AND MONTH(e.date) = :month
              AND YEAR(e.date) = :year
            """)
    BigDecimal sumByUserIdAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);

    // Monthly totals grouped by category (used for pie chart)
    @Query("""
            SELECT e.category, COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
              AND MONTH(e.date) = :month
              AND YEAR(e.date) = :year
            GROUP BY e.category
            """)
    List<Object[]> sumByCategoryForMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);

    // Last 6 months totals (for trend chart)
    @Query("""
            SELECT MONTH(e.date), YEAR(e.date), COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.user.id = :userId
              AND e.date >= :fromDate
            GROUP BY YEAR(e.date), MONTH(e.date)
            ORDER BY YEAR(e.date), MONTH(e.date)
            """)
    List<Object[]> monthlyTotalsFrom(
            @Param("userId") Long userId,
            @Param("fromDate") LocalDate fromDate);
}
