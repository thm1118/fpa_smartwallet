package com.fintech.smartwallet.dto;

import com.fintech.smartwallet.entity.Account;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {
    private Long id;
    private String name;
    private Account.AccountType type;
    private BigDecimal balance;
    private String description;
}
