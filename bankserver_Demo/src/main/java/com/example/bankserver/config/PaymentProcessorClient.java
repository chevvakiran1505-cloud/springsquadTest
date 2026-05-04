package com.example.bankserver.config;

import com.example.bankserver.dto.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PaymentProcessorClient {

    @Autowired
    private RestClient paymentRestClient;

    public void processPayment(TransactionRequest req) {
        // Simulate external payment processing
        // In real implementation, call external API
        if (req.getAmount().compareTo(java.math.BigDecimal.valueOf(1000)) > 0) {
            throw new RuntimeException("Payment failed");
        }
    }
}