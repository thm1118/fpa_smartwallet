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
 * TradeSim服务客户端 —— 封装对TradeSim内部接口的调用
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TradeSimClient {

    private final RestTemplate restTemplate;

    @Value("${service.tradesim.url}")
    private String tradesimUrl;

    @Value("${service.internal-key}")
    private String internalKey;

    /**
     * 调用TradeSim内部接口，获取用户投资组合市值
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPortfolioValue(String username) {
        String url = tradesimUrl + "/internal/account/portfolio-value?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-Key", internalKey);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling TradeSim portfolio-value for user: {}", username);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch TradeSim portfolio value: {}", e.getMessage());
            return Map.of("totalAssets", BigDecimal.ZERO, "available", false);
        }
    }

    /**
     * 调用TradeSim内部接口，获取用户持仓列表
     */
    @SuppressWarnings("unchecked")
    public List<Object> getPositions(String username) {
        String url = tradesimUrl + "/internal/account/positions?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-Key", internalKey);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling TradeSim positions for user: {}", username);
        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch TradeSim positions: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 调用TradeSim公开行情接口，获取证券列表（无需认证）
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMarketSecurities() {
        String url = tradesimUrl + "/api/market/securities";

        log.info("Calling TradeSim public market securities");
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch TradeSim market securities: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
