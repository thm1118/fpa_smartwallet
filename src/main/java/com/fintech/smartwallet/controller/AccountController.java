package com.fintech.smartwallet.controller;

import com.fintech.smartwallet.client.TradeSimClient;
import com.fintech.smartwallet.dto.AccountDTO;
import com.fintech.smartwallet.dto.AssetOverviewDTO;
import com.fintech.smartwallet.security.CurrentUser;
import com.fintech.smartwallet.security.UserPrincipal;
import com.fintech.smartwallet.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final TradeSimClient tradeSimClient;

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAccounts(@CurrentUser UserPrincipal currentUser) {
        List<AccountDTO> accounts = accountService.getUserAccounts(currentUser.getUser());
        return ResponseEntity.ok(accounts);
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(
        @CurrentUser UserPrincipal currentUser,
        @Valid @RequestBody AccountDTO accountDTO
    ) {
        AccountDTO created = accountService.createAccount(currentUser.getUser(), accountDTO);
        return ResponseEntity.ok(created);
    }

    /**
     * 资产总览 —— 聚合钱包账户余额 + TradeSim投资市值
     */
    @GetMapping("/asset-overview")
    public ResponseEntity<AssetOverviewDTO> getAssetOverview(@CurrentUser UserPrincipal currentUser) {
        // 获取钱包账户数据
        List<AccountDTO> walletAccounts = accountService.getUserAccounts(currentUser.getUser());
        BigDecimal walletTotal = walletAccounts.stream()
                .map(AccountDTO::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 调用TradeSim获取投资组合市值
        Map<String, Object> portfolioData = tradeSimClient.getPortfolioValue(
                currentUser.getUser().getUsername());

        BigDecimal investmentAssets = BigDecimal.ZERO;
        boolean investmentAvailable = false;
        if (portfolioData != null && Boolean.TRUE.equals(portfolioData.get("available"))) {
            investmentAvailable = true;
            Object totalAssetsObj = portfolioData.get("totalAssets");
            if (totalAssetsObj != null) {
                investmentAssets = new BigDecimal(totalAssetsObj.toString());
            }
        }

        AssetOverviewDTO overview = new AssetOverviewDTO();
        overview.setWalletTotalBalance(walletTotal);
        overview.setWalletAccounts(walletAccounts);
        overview.setInvestmentTotalAssets(investmentAssets);
        overview.setInvestmentAvailable(investmentAvailable);
        overview.setGrandTotal(walletTotal.add(investmentAssets));

        return ResponseEntity.ok(overview);
    }
}
