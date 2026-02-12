package com.fintech.smartwallet.repository;

import com.fintech.smartwallet.entity.Category;
import com.fintech.smartwallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(Transaction.TransactionType type);
}
