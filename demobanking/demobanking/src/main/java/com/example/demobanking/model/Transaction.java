package com.example.demobanking.model;

public record Transaction(
        String transactionId,
        String accountId,
        String type,
        double amount
) {}
