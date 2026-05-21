package com.fintech.smartwallet.repository;

import com.fintech.smartwallet.entity.Account;
import com.fintech.smartwallet.entity.Transaction;
import com.fintech.smartwallet.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountIn(List<Account> accounts, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account IN :accounts " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountsAndDateRange(
        @Param("accounts") List<Account> accounts,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Sum EXPENSE transaction amounts for a given user, category and date range.
     * Used by TxnRecordedListener to recompute budget spent.
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user = :user " +
           "AND t.category.id = :categoryId " +
           "AND t.type = com.fintech.smartwallet.entity.Transaction$TransactionType.EXPENSE " +
           "AND t.transactionDate >= :start AND t.transactionDate < :end")
    BigDecimal sumExpenseByUserAndCategoryAndDateRange(
        @Param("user") User user,
        @Param("categoryId") Long categoryId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /** Find all transactions for a user's accounts (used by autoRecordFromPayment). */
    List<Transaction> findByAccountIn(List<Account> accounts);
}
