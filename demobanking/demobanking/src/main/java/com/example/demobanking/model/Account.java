package com.example.demobanking.model;

public record Account(
        String accountId,
        String ownerId,
        String accountType,
        double balance
) {}

