package com.example.bankserver.dto;

import com.example.bankserver.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private String accountId;
    private AccountType accountType;
    private String currency;
    private BigDecimal balance;
    private Instant createdAt;
}
