package com.example.demobanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
public record CreateTransactionRequest(

        @NotBlank(message = "Account ID is required")
        String accountId,

        @NotBlank(message = "Transaction type is required")
        @Pattern(
                regexp = "DEBIT|CREDIT",
                message = "Transaction type must be DEBIT or CREDIT"
        )
        String type,

        @Positive(message = "Amount must be greater than zero")
        double amount,

        @NotBlank(message = "Reference is required")
        @Size(min = 3, max = 50, message = "Reference must be between 3 and 50 characters")
        String reference
) {}
