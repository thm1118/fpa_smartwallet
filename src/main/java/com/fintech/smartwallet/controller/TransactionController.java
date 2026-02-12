package com.fintech.smartwallet.controller;

import com.fintech.smartwallet.client.QuickPayClient;
import com.fintech.smartwallet.dto.TransactionDTO;
import com.fintech.smartwallet.security.CurrentUser;
import com.fintech.smartwallet.security.UserPrincipal;
import com.fintech.smartwallet.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final QuickPayClient quickPayClient;

    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getTransactions(
        @CurrentUser UserPrincipal currentUser,
        Pageable pageable
    ) {
        Page<TransactionDTO> transactions = transactionService.getUserTransactions(
            currentUser.getUser(), pageable
        );
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
        @CurrentUser UserPrincipal currentUser,
        @Valid @RequestBody TransactionDTO transactionDTO
    ) {
        TransactionDTO created = transactionService.createTransaction(
            currentUser.getUser(), transactionDTO
        );
        return ResponseEntity.ok(created);
    }

    /**
     * 通过QuickPay支付 —— 调用QuickPay内部转账接口完成实际资金划转
     * 请求体: { "fromAccountNo": "QP...", "toAccountNo": "QP...", "amount": 100.00, "walletAccountId": 1 }
     */
    @PostMapping("/pay-via-quickpay")
    public ResponseEntity<?> payViaQuickPay(
            @CurrentUser UserPrincipal currentUser,
            @RequestBody Map<String, Object> request) {

        String fromAccountNo = (String) request.get("fromAccountNo");
        String toAccountNo = (String) request.get("toAccountNo");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        // 调用QuickPay内部转账接口
        Map<String, Object> result = quickPayClient.transfer(fromAccountNo, toAccountNo, amount);

        // 同时在SmartWallet中记录一笔支出（如果指定了钱包账户）
        if (Boolean.TRUE.equals(result.get("success")) && request.get("walletAccountId") != null) {
            Long walletAccountId = Long.valueOf(request.get("walletAccountId").toString());
            TransactionDTO record = new TransactionDTO();
            record.setAccountId(walletAccountId);
            record.setType(com.fintech.smartwallet.entity.Transaction.TransactionType.EXPENSE);
            record.setAmount(amount);
            record.setDescription("Pay via QuickPay to " + toAccountNo);
            record.setTransactionDate(java.time.LocalDateTime.now());
            transactionService.createTransaction(currentUser.getUser(), record);
        }

        return ResponseEntity.ok(result);
    }
}
