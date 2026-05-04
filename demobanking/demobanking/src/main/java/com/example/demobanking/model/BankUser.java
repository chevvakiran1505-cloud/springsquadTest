package com.example.demobanking.model;

public record BankUser(
        String userId,
        String username,
        String email,
        String role
) {}