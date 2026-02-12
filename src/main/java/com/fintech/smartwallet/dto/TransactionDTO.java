package com.fintech.smartwallet.dto;

import com.fintech.smartwallet.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private Long accountId;
    private String accountName;
    private Long categoryId;
    private String categoryName;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;
}
