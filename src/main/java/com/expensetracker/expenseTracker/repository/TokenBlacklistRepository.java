package com.expensetracker.expenseTracker.repository;

import com.expensetracker.expenseTracker.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface TokenBlacklistRepository
        extends JpaRepository<TokenBlacklist, Long> {

    // Check if a token hash exists in the blacklist
    boolean existsByTokenHash(String tokenHash);

    // Delete all expired tokens — called by scheduled cleanup job
    @Modifying
    @Transactional
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiresAt < :now")
    void deleteAllExpiredBefore(LocalDateTime now);
}