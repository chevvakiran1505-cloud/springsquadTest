package com.example.demobanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(

        @NotBlank(message = "Owner ID is required")
        String ownerId,

        @NotBlank(message = "Account type is required")
        @Pattern(
                regexp = "CHECKING|SAVINGS|INVESTMENT",
                message = "Account type must be CHECKING, SAVINGS, or INVESTMENT"
        )
        String accountType,

        @Positive(message = "Initial deposit must be greater than zero")
        double initialDeposit,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description

) {}
