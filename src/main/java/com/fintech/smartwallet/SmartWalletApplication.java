package com.fintech.smartwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartWalletApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartWalletApplication.class, args);
    }
}
