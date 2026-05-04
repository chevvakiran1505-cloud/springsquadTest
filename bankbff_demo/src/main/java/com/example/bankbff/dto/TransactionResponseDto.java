package com.example.bankbff.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponseDto(
    String transactionId,
    String accountId,
    String type,
    BigDecimal amount,
    String status,
    String counterparty,
    String transferGroupId,
    String description,
    Instant createdAt
) {}
