package com.fintech.smartwallet.controller;

import com.fintech.smartwallet.client.MiddlePlatformClient;
import com.fintech.smartwallet.client.QuickPayClient;
import com.fintech.smartwallet.client.TradeSimClient;
import com.fintech.smartwallet.dto.AccountDTO;
import com.fintech.smartwallet.dto.AssetOverviewDTO;
import com.fintech.smartwallet.security.CurrentUser;
import com.fintech.smartwallet.security.UserPrincipal;
import com.fintech.smartwallet.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移动端银行BFF控制器 —— 聚合各微服务数据，为H5前端提供专属接口
 */
@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
public class MobileBankController {

    private final AccountService accountService;
    private final QuickPayClient quickPayClient;
    private final TradeSimClient tradeSimClient;
    private final MiddlePlatformClient middlePlatformClient;

    /**
     * 首页聚合数据：钱包余额 + QuickPay账户 + TradeSim投资市值
     */
    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getHome(@CurrentUser UserPrincipal currentUser) {
        String username = currentUser.getUser().getUsername();
        String customerNo = currentUser.getUser().getCustomerNo();

        // 钱包账户余额
        List<AccountDTO> walletAccounts = accountService.getUserAccounts(currentUser.getUser());
        BigDecimal walletTotal = walletAccounts.stream()
                .map(AccountDTO::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // QuickPay账户
        Map<String, Object> quickPayAccount = quickPayClient.getAccount(username);

        // TradeSim投资市值
        Map<String, Object> portfolioData = tradeSimClient.getPortfolioValue(username);
        BigDecimal investmentAssets = BigDecimal.ZERO;
        boolean investmentAvailable = false;
        if (portfolioData != null && Boolean.TRUE.equals(portfolioData.get("available"))) {
            investmentAvailable = true;
            Object totalAssetsObj = portfolioData.get("totalAssets");
            if (totalAssetsObj != null) {
                investmentAssets = new BigDecimal(totalAssetsObj.toString());
            }
        }

        BigDecimal grandTotal = walletTotal.add(investmentAssets);

        Map<String, Object> result = new HashMap<>();
        result.put("walletTotalBalance", walletTotal);
        result.put("walletAccounts", walletAccounts);
        result.put("quickPayAccount", quickPayAccount);
        result.put("investmentTotalAssets", investmentAssets);
        result.put("investmentAvailable", investmentAvailable);
        result.put("grandTotal", grandTotal);
        result.put("customerNo", customerNo);

        return ResponseEntity.ok(result);
    }

    /**
     * QuickPay账户信息
     */
    @GetMapping("/payment/account")
    public ResponseEntity<Map<String, Object>> getPaymentAccount(@CurrentUser UserPrincipal currentUser) {
        String username = currentUser.getUser().getUsername();
        Map<String, Object> account = quickPayClient.getAccount(username);
        return ResponseEntity.ok(account);
    }

    /**
     * QuickPay交易记录
     */
    @GetMapping("/payment/transactions")
    public ResponseEntity<List<Object>> getPaymentTransactions(@CurrentUser UserPrincipal currentUser) {
        String username = currentUser.getUser().getUsername();
        List<Object> transactions = quickPayClient.getTransactions(username);
        return ResponseEntity.ok(transactions);
    }

    /**
     * 绑定银行卡列表
     */
    @GetMapping("/payment/cards")
    public ResponseEntity<List<Object>> getPaymentCards(@CurrentUser UserPrincipal currentUser) {
        String username = currentUser.getUser().getUsername();
        List<Object> cards = quickPayClient.getCards(username);
        return ResponseEntity.ok(cards);
    }

    /**
     * 投资持仓列表
     */
    @GetMapping("/investment/positions")
    public ResponseEntity<List<Object>> getInvestmentPositions(@CurrentUser UserPrincipal currentUser) {
        String username = currentUser.getUser().getUsername();
        List<Object> positions = tradeSimClient.getPositions(username);
        return ResponseEntity.ok(positions);
    }

    /**
     * 投资组合市值
     */
    @GetMapping("/investment/portfolio")
    public ResponseEntity<Map<String, Object>> getInvestmentPortfolio(@CurrentUser UserPrincipal currentUser) {
        String username = currentUser.getUser().getUsername();
        Map<String, Object> portfolio = tradeSimClient.getPortfolioValue(username);
        return ResponseEntity.ok(portfolio);
    }

    /**
     * 行情证券列表（公开接口，无需外部认证）
     */
    @GetMapping("/market/securities")
    public ResponseEntity<List<Object>> getMarketSecurities() {
        List<Object> securities = tradeSimClient.getMarketSecurities();
        return ResponseEntity.ok(securities);
    }

    /**
     * 用户消息列表
     */
    @GetMapping("/messages")
    public ResponseEntity<List<Object>> getMessages(@CurrentUser UserPrincipal currentUser) {
        String customerNo = currentUser.getUser().getCustomerNo();
        List<Object> messages = middlePlatformClient.getMessages(customerNo);
        return ResponseEntity.ok(messages);
    }

    /**
     * 产品推荐列表
     */
    @GetMapping("/products/recommend")
    public ResponseEntity<List<Object>> getProductsRecommend(@CurrentUser UserPrincipal currentUser) {
        // 优先从客户档案取客户等级，取不到则使用默认等级
        String customerNo = currentUser.getUser().getCustomerNo();
        String customerLevel = "REGULAR";
        try {
            Map<String, Object> profile = middlePlatformClient.getCustomerProfile(customerNo);
            if (profile != null && profile.get("customerLevel") != null) {
                customerLevel = profile.get("customerLevel").toString();
            }
        } catch (Exception e) {
            // 降级为默认等级
        }
        List<Object> products = middlePlatformClient.getProductRecommend(customerLevel);
        return ResponseEntity.ok(products);
    }

    /**
     * 客户档案
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@CurrentUser UserPrincipal currentUser) {
        String customerNo = currentUser.getUser().getCustomerNo();
        Map<String, Object> profile = middlePlatformClient.getCustomerProfile(customerNo);
        return ResponseEntity.ok(profile);
    }
}
