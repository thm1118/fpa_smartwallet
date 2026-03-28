package com.fintech.smartwallet.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
}
