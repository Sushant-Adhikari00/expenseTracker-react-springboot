package com.expensetracker.expenseTracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store SHA-256 hash of token — not raw token for security
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    // When the original JWT expires — used for cleanup
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "blacklisted_at", updatable = false)
    private LocalDateTime blacklistedAt;
}