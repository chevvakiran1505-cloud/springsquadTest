package com.example.demobanking.dto;
import com.example.demobanking.entity.TransactionEntity;
import com.example.demobanking.entity.TransactionStatus;
import com.example.demobanking.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public class TransactionResponse {

    private String transactionId;
    private String accountId;
    private  TransactionType type;
    private  BigDecimal amount ;
    private  TransactionStatus status;
    private  String counterparty;
    private String transferGroupId;
    private  String description;
    private  LocalDateTime createdAt;

    public TransactionResponse(){

    }
    public TransactionResponse(TransactionEntity entity){
        this.transactionId = entity.getTransactionId();
        this.accountId = entity.getAccountId();
        this.type = entity.getType();
        this.amount = entity.getAmount();
        this.status = entity.getStatus();
        this.counterparty = entity.getCounterparty();
        this.transferGroupId = entity.getTransferGroupId();
        this.description = entity.getDescription();
         this.createdAt = entity.getCreatedAt();
    }
    public  String getTransactionId() {
        return  transactionId;
    }
    public void setTransactionId(String transactionId){
        this.transactionId = transactionId;
    }
    public  String getAccountId() {
        return accountId;
    }
    public  void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }
    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }
    public BigDecimal getAmount() {
        return  amount;
    }
    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }
    public  TransactionStatus getStatus() {
        return status;
    }
    public void setStatus(TransactionStatus status){
        this.status = status;
    }
    public String getCounterParty() {
        return counterparty;
    }
    public void setCounterparty (String counterparty){
        this.counterparty = counterparty;
    }
    public String getTransferGroupId() {
        return  transferGroupId;
    }
    public  void setTransferGroupId(String transferGroupId)
    {
        this.transferGroupId = transferGroupId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String  description)
    {
        this.description = description;
    }
    public  LocalDateTime getCreatedAt() {
        return  createdAt;
    }
    public  void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
}
