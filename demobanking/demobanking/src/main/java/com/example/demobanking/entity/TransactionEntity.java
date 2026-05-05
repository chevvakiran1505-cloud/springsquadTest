package com.example.demobanking.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(
        name ="TRANSACTIONS"
)
public class TransactionEntity {
    @Id
    @Column(name ="TRANSACTION_ID", nullable= false, length = 64)
    private String transactionId;
    @Column(name ="ACCOUNT_ID", nullable= false, length = 64)
    private String accountId;
    /*@Column(name ="ACCOUNT_TYPE", nullable= false, length = 16)*/
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 16)
    private TransactionType type;
    @Column(name ="AMOUNT", nullable= false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 16)
    private TransactionStatus status;
    @Column(name ="COUNTERPARTY",  length = 64)
    private String counterparty;
    @Column(name ="TRANSFER_GROUP_ID",  length = 64)
    private String transferGroupId;
    @Column(name ="DESCRIPTION",  length = 255)
    private String description;
    @Column(name ="CREATED_AT", nullable= false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public TransactionEntity() {

    }
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId)
    {
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
    public void setCounterParty(String counterparty)
    {
        this.counterparty = counterparty;
    }
    public  String getTransferGroupId(){
        return transferGroupId;
    }
    public void setTransferGroupId(String transferGroupId){
        this.transferGroupId = transferGroupId;
    }
    public String getDescription() {
        return description;
    }
    public  void setDescription(String description){
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
