package com.fintech.smartwallet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AssetOverviewDTO {
    private BigDecimal walletTotalBalance;
    private List<AccountDTO> walletAccounts;
    private BigDecimal investmentTotalAssets;
    private boolean investmentAvailable;
    private BigDecimal grandTotal;
}
