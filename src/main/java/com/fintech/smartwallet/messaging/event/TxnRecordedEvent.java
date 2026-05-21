package com.fintech.smartwallet.messaging.event;

import java.math.BigDecimal;

public class TxnRecordedEvent {

    private Long transactionId;
    private Long userId;
    private Long accountId;
    private String type;
    private BigDecimal amount;
    private Long categoryId;

    public TxnRecordedEvent() {
    }

    public TxnRecordedEvent(Long transactionId, Long userId, Long accountId,
                             String type, BigDecimal amount, Long categoryId) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.categoryId = categoryId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
