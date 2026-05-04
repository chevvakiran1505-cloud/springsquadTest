package com.example.bankserver.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    private final String accountId;
    private final BigDecimal balance;
    private final BigDecimal amount;

    public InsufficientFundsException(String accountId, BigDecimal balance, BigDecimal amount) {
        super(String.format("Account %s has insufficient funds: balance=%.2f, required=%.2f", accountId, balance, amount));
        this.accountId = accountId;
        this.balance = balance;
        this.amount = amount;
    }

    public InsufficientFundsException() {
        super("Insufficient funds");
        this.accountId = null;
        this.balance = null;
        this.amount = null;
    }

    public InsufficientFundsException(String message) {
        super(message);
        this.accountId = null;
        this.balance = null;
        this.amount = null;
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
        this.accountId = null;
        this.balance = null;
        this.amount = null;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
