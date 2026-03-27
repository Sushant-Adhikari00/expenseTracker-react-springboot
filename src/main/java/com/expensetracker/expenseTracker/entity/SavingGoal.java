package com.expensetracker.expenseTracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "saving_goals")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SavingGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;              // e.g. "Emergency Fund", "New Laptop"

    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal savedAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate targetDate;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Utility: calculate progress percentage
    @Transient
    public double getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) return 0;
        return savedAmount.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public enum GoalStatus {
        IN_PROGRESS, COMPLETED, CANCELLED
    }
}