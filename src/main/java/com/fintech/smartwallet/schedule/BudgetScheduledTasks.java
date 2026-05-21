package com.fintech.smartwallet.schedule;

import com.fintech.smartwallet.entity.Budget;
import com.fintech.smartwallet.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BudgetScheduledTasks {

    private final BudgetRepository budgetRepository;

    /**
     * 月度预算结转 — runs at 02:00 on the 1st of every month.
     * For every budget that existed in the previous month, creates a corresponding
     * budget row for the current month (same user / category / amount / alert settings),
     * with spent reset to 0.  Skips rows that already exist.
     */
    @Scheduled(cron = "0 0 2 1 * *")
    @Transactional
    public void rolloverMonthlyBudgets() {
        LocalDate today = LocalDate.now();
        // "previous month" relative to the 1st of the current month
        LocalDate prevMonth = today.minusMonths(1);
        int prevYear  = prevMonth.getYear();
        int prevMon   = prevMonth.getMonthValue();
        int curYear   = today.getYear();
        int curMon    = today.getMonthValue();

        log.info("BudgetScheduledTasks.rolloverMonthlyBudgets: rolling over budgets from {}/{} to {}/{}",
                prevYear, prevMon, curYear, curMon);

        List<Budget> previousBudgets = budgetRepository.findByYearAndMonth(prevYear, prevMon);

        int created = 0;
        int skipped = 0;
        for (Budget prev : previousBudgets) {
            Long categoryId = (prev.getCategory() != null) ? prev.getCategory().getId() : null;

            Optional<Budget> existing = budgetRepository.findByUserAndCategoryIdAndYearAndMonth(
                    prev.getUser(), categoryId, curYear, curMon);

            if (existing.isPresent()) {
                skipped++;
                continue;
            }

            Budget next = new Budget();
            next.setUser(prev.getUser());
            next.setCategory(prev.getCategory());
            next.setAmount(prev.getAmount());
            next.setYear(curYear);
            next.setMonth(curMon);
            next.setSpent(BigDecimal.ZERO);
            next.setAlertEnabled(prev.getAlertEnabled());
            next.setAlertThreshold(prev.getAlertThreshold());

            budgetRepository.save(next);
            created++;
        }

        log.info("BudgetScheduledTasks.rolloverMonthlyBudgets: completed — created={}, skipped={}", created, skipped);
    }

    /**
     * 预算超支预警扫描 — runs daily at 09:00.
     * Scans current-month budgets where alertEnabled=true and
     * spent / amount >= alertThreshold / 100.  Logs a WARN for each hit.
     * (Kafka event publishing is handled in a later phase.)
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void scanBudgetOverspendAlerts() {
        LocalDate today = LocalDate.now();
        int year  = today.getYear();
        int month = today.getMonthValue();

        log.info("BudgetScheduledTasks.scanBudgetOverspendAlerts: scanning budgets for {}/{}", year, month);

        List<Budget> alertBudgets =
                budgetRepository.findByYearAndMonthAndAlertEnabledTrue(year, month);

        int alertCount = 0;
        for (Budget budget : alertBudgets) {
            if (budget.getAmount() == null || budget.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            BigDecimal spent     = budget.getSpent() != null ? budget.getSpent() : BigDecimal.ZERO;
            BigDecimal threshold = budget.getAlertThreshold() != null
                    ? budget.getAlertThreshold()
                    : new BigDecimal("80.00");

            // spentRatio = spent / amount * 100  (percentage)
            BigDecimal spentRatio = spent
                    .multiply(new BigDecimal("100"))
                    .divide(budget.getAmount(), 2, RoundingMode.HALF_UP);

            if (spentRatio.compareTo(threshold) >= 0) {
                String username    = budget.getUser() != null ? budget.getUser().getUsername() : "unknown";
                String categoryName = (budget.getCategory() != null)
                        ? budget.getCategory().getName()
                        : "uncategorized";

                log.warn("BudgetAlert: user={}, category={}, spent={}, amount={}, ratio={}%, threshold={}%",
                        username, categoryName, spent, budget.getAmount(), spentRatio, threshold);
                alertCount++;
            }
        }

        log.info("BudgetScheduledTasks.scanBudgetOverspendAlerts: completed — alerts fired={}", alertCount);
    }
}
