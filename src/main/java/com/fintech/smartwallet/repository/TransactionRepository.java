package com.fintech.smartwallet.repository;

import com.fintech.smartwallet.entity.Account;
import com.fintech.smartwallet.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
