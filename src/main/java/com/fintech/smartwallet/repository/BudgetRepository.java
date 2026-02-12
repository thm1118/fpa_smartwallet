package com.fintech.smartwallet.repository;

import com.fintech.smartwallet.entity.Budget;
import com.fintech.smartwallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserAndYearAndMonth(User user, Integer year, Integer month);
    Optional<Budget> findByUserAndCategoryIdAndYearAndMonth(
        User user, Long categoryId, Integer year, Integer month
    );
}
