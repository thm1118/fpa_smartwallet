package com.fintech.smartwallet.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MiddlePlatform服务客户端 —— 封装对MiddlePlatform内部接口的调用
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MiddlePlatformClient {

    private final RestTemplate restTemplate;

    @Value("${service.middleplatform.url}")
    private String middleplatformUrl;

    @Value("${service.internal-key}")
    private String internalKey;

    /**
     * 调用MiddlePlatform内部接口，获取客户档案信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCustomerProfile(String customerNo) {
        String url = middleplatformUrl + "/internal/customer/profile?customerNo=" + customerNo;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-Key", internalKey);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling MiddlePlatform customer-profile for customerNo: {}", customerNo);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch MiddlePlatform customer profile: {}", e.getMessage());
            return Map.of("available", false);
        }
    }

    /**
     * 调用MiddlePlatform内部接口，获取产品推荐列表
     */
    @SuppressWarnings("unchecked")
    public List<Object> getProductRecommend(String customerLevel) {
        String url = middleplatformUrl + "/internal/product/recommend?customerLevel=" + customerLevel;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-Key", internalKey);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling MiddlePlatform product-recommend for customerLevel: {}", customerLevel);
        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch MiddlePlatform product recommend: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 调用MiddlePlatform内部接口，获取客户消息列表
     */
    @SuppressWarnings("unchecked")
    public List<Object> getMessages(String customerNo) {
        String url = middleplatformUrl + "/internal/customer/messages?customerNo=" + customerNo;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Service-Key", internalKey);

        HttpEntity<?> request = new HttpEntity<>(headers);

        log.info("Calling MiddlePlatform messages for customerNo: {}", customerNo);
        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("Failed to fetch MiddlePlatform messages: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
