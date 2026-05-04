package com.example.bankserver.dto;

import com.example.bankserver.model.TransactionStatus;
import com.example.bankserver.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {

    private String transactionId;
    private String accountId;
    private TransactionType type;
    private BigDecimal amount;
    private TransactionStatus status;
    private String counterparty;
    private String transferGroupId;
    private String description;
    private Instant createdAt;

    public TransactionResponse() {
    }

    public TransactionResponse(String transactionId,
                               String accountId,
                               TransactionType type,
                               BigDecimal amount,
                               TransactionStatus status,
                               String counterparty,
                               String transferGroupId,
                               String description,
                               Instant createdAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.counterparty = counterparty;
        this.transferGroupId = transferGroupId;
        this.description = description;
        this.createdAt = createdAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getTransferGroupId() {
        return transferGroupId;
    }

    public void setTransferGroupId(String transferGroupId) {
        this.transferGroupId = transferGroupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}