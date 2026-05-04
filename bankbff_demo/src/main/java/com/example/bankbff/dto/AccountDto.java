package com.example.bankbff.dto;

import java.math.BigDecimal;
import java.time.Instant;


/**
 * Account DTO returned by the BFF to the SPA.
 *
 * Mirrors the shape of the Account record returned by the resource server.
 * In a more sophisticated BFF you might transform the shape here, but for
 * Lab 4.6 we just pass the data through.
 */
public record AccountDto(
        String accountId,
        String accountType,
        String currency,
        BigDecimal balance,
        Instant createdAt
) {
}