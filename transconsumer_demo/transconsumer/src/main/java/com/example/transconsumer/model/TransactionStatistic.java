package com.example.transconsumer.model;

import java.math.BigDecimal;

public record TransactionStatistic(
        TransactionType type,
        BigDecimal amount
) {}