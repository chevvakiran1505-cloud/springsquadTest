package com.example.bankbff.dto;

public record TransferRequestDto(
        String fromAccountNumber,
        String toAccountNumber,
        double amount
) {
}