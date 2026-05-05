package com.example.demobanking.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(
        name ="ACCOUNTS"
 )
public class AccountEntity {
    @Id
    @Column(name ="ACCOUNT_ID", nullable= false, length = 64)
    private String accountId;
    @Column(name ="OWNER_ID", nullable= false, length = 64)
    private String ownerId;
    @Column(name ="ACCOUNT_TYPE", nullable= false, length = 16)
    private String accountType;
    @Column(name ="CURRENCY", nullable= false, length = 3)
    private String currency;
    @Column(name ="BALANCE", nullable= false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name ="CREATED_AT", nullable= false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public AccountEntity() {

    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

