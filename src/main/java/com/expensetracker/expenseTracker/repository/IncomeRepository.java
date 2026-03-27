package com.expensetracker.expenseTracker.repository;

import com.expensetracker.expenseTracker.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserIdOrderByDateDesc(Long userId);

    // Fetch incomes within a date range for a user
    List<Income> findByUserIdAndDateBetweenOrderByDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    // Sum of income for a given month/year
    @Query("""
            SELECT COALESCE(SUM(i.amount), 0)
            FROM Income i
            WHERE i.user.id = :userId
              AND MONTH(i.date) = :month
              AND YEAR(i.date) = :year
            """)
    BigDecimal sumByUserIdAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);

    // Monthly totals grouped by category
    @Query("""
            SELECT i.category, COALESCE(SUM(i.amount), 0)
            FROM Income i
            WHERE i.user.id = :userId
              AND MONTH(i.date) = :month
              AND YEAR(i.date) = :year
            GROUP BY i.category
            """)
    List<Object[]> sumByCategoryForMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);
}