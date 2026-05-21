package com.fintech.smartwallet.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * QuickPay服务客户端 —— 封装对QuickPay内部接口的调用
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuickPayClient {

    private final RestTemplate restTemplate;

    @Value("${service.quickpay.url}")
    private String quickpayUrl;

    @Value("${service.internal-key}")
    private String internalKey;

    /**
     * 调用QuickPay内部转账接口
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> transfer(String fromAccountNo, String toAccountNo, BigDecimal amount) {
        String url = quickpayUrl + "/internal/payment/transfer";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Service-Key", internalKey);

        Map<String, Object> body = Map.of(
                "fromAccountNo", fromAccountNo,
                "toAccountNo", toAccountNo,
                "amount", amount
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        log.info("Calling QuickPay transfer: {} -> {}, amount={}", fromAccountNo, toAccountNo, amount);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        return response.getBody();
    }

    /**
     * 获取用户QuickPay账户信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAccount(String username) {
        String url = quickpayUrl + "/internal/payment/account?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-Key", internalKey);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling QuickPay getAccount for user: {}", username);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch QuickPay account: {}", e.getMessage());
            return Map.of("available", false);
        }
    }

    /**
     * 获取用户QuickPay交易记录
     */
    @SuppressWarnings("unchecked")
    public List<Object> getTransactions(String username) {
        String url = quickpayUrl + "/internal/payment/transactions?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-Key", internalKey);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling QuickPay getTransactions for user: {}", username);
        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch QuickPay transactions: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 获取用户绑定的银行卡列表
     */
    @SuppressWarnings("unchecked")
    public List<Object> getCards(String username) {
        String url = quickpayUrl + "/internal/payment/cards?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-Key", internalKey);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling QuickPay getCards for user: {}", username);
        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch QuickPay cards: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
