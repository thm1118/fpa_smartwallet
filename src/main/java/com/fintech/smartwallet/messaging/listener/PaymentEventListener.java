package com.fintech.smartwallet.messaging.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.smartwallet.entity.User;
import com.fintech.smartwallet.messaging.event.PaymentEvent;
import com.fintech.smartwallet.repository.UserRepository;
import com.fintech.smartwallet.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Cross-service listener: consumes PaymentEvent published by QuickPay on
 * "fintech.payment-events" and auto-records a personal-finance EXPENSE for
 * the matching SmartWallet user so their spending history stays current.
 */
@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public PaymentEventListener(ObjectMapper objectMapper,
                                 UserRepository userRepository,
                                 TransactionService transactionService) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "fintech.payment-events")
    public void onPaymentEvent(String message) {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);

            if (event.getUsername() == null || event.getUsername().isBlank()) {
                log.warn("PaymentEventListener: received event with no username, skipping");
                return;
            }

            Optional<User> userOpt = userRepository.findByUsername(event.getUsername());
            if (userOpt.isEmpty()) {
                log.info("PaymentEventListener: no SmartWallet user found for username '{}', skipping",
                        event.getUsername());
                return;
            }

            User user = userOpt.get();
            log.debug("PaymentEventListener: auto-recording EXPENSE for user={} txn={}",
                    user.getUsername(), event.getTransactionNo());

            transactionService.autoRecordFromPayment(event, user);

        } catch (Exception e) {
            log.error("PaymentEventListener failed to process message: {}", e.getMessage(), e);
        }
    }
}
