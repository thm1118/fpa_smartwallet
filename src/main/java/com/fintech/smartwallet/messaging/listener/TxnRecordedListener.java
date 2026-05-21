package com.fintech.smartwallet.messaging.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.smartwallet.entity.Budget;
import com.fintech.smartwallet.entity.Transaction;
import com.fintech.smartwallet.entity.User;
import com.fintech.smartwallet.messaging.event.TxnRecordedEvent;
import com.fintech.smartwallet.repository.AccountRepository;
import com.fintech.smartwallet.repository.BudgetRepository;
import com.fintech.smartwallet.repository.TransactionRepository;
import com.fintech.smartwallet.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

/**
 * Intra-service listener: consumes TxnRecordedEvent and keeps the
 * corresponding Budget's spent amount up to date.  If the budget
 * has exceeded its alert threshold an over-budget warning is logged.
 */
@Component
public class TxnRecordedListener {

    private static final Logger log = LoggerFactory.getLogger(TxnRecordedListener.class);

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TxnRecordedListener(ObjectMapper objectMapper,
                                UserRepository userRepository,
                                BudgetRepository budgetRepository,
                                TransactionRepository transactionRepository,
                                AccountRepository accountRepository) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @KafkaListener(topics = "smartwallet.txn-recorded")
    public void onTxnRecorded(String message) {
        try {
            TxnRecordedEvent event = objectMapper.readValue(message, TxnRecordedEvent.class);

            // Only EXPENSE transactions affect budgets
            if (!Transaction.TransactionType.EXPENSE.name().equals(event.getType())) {
                return;
            }

            // categoryId is required to locate a budget
            if (event.getCategoryId() == null) {
                return;
            }

            Optional<User> userOpt = userRepository.findById(event.getUserId());
            if (userOpt.isEmpty()) {
                log.warn("TxnRecordedListener: user {} not found, skipping budget update", event.getUserId());
                return;
            }
            User user = userOpt.get();

            YearMonth now = YearMonth.now();
            int year = now.getYear();
            int month = now.getMonthValue();

            Optional<Budget> budgetOpt = budgetRepository.findByUserAndCategoryIdAndYearAndMonth(
                    user, event.getCategoryId(), year, month);

            if (budgetOpt.isEmpty()) {
                // No budget configured for this category/period — nothing to update
                return;
            }
            Budget budget = budgetOpt.get();

            // Recompute spent as the sum of all EXPENSE amounts for user+category in the current month
            LocalDateTime start = now.atDay(1).atStartOfDay();
            LocalDateTime end = now.plusMonths(1).atDay(1).atStartOfDay();
            BigDecimal spent = transactionRepository.sumExpenseByUserAndCategoryAndDateRange(
                    user, event.getCategoryId(), start, end);

            budget.setSpent(spent);
            budgetRepository.save(budget);

            log.debug("Budget {} updated: spent={} / amount={}", budget.getId(), spent, budget.getAmount());

            // Over-budget alert check
            if (Boolean.TRUE.equals(budget.getAlertEnabled()) && budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                // spent / amount >= alertThreshold / 100
                BigDecimal spentRatio = spent.multiply(new BigDecimal("100"))
                        .divide(budget.getAmount(), 2, java.math.RoundingMode.HALF_UP);
                if (spentRatio.compareTo(budget.getAlertThreshold()) >= 0) {
                    log.warn("OVER-BUDGET ALERT: user={} category={} period={}/{} spent={} budget={} ({}% >= {}%)",
                            user.getUsername(), event.getCategoryId(), year, month,
                            spent, budget.getAmount(), spentRatio, budget.getAlertThreshold());
                }
            }

        } catch (Exception e) {
            log.error("TxnRecordedListener failed to process message: {}", e.getMessage(), e);
        }
    }
}
