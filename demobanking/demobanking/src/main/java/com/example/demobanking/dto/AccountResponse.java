package com.example.demobanking.dto;

import com.example.demobanking.entity.AccountEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class AccountResponse {

    private  String accountId;
    private  String ownerId;
    private String accountType;
    private String currency;
    private  BigDecimal balance;
    private LocalDateTime createdAt;
    public AccountResponse(){

    }
    public AccountResponse(AccountEntity entity){
          this.accountId = entity.getAccountId();
          this.ownerId = entity.getOwnerId();
          this.accountType = entity.getAccountType();
          this.currency = entity.getCurrency();
          this.balance = entity.getBalance();
          this.createdAt = entity.getCreatedAt();
    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId){
        this.accountId = accountId;
    }
    public  String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId){
        this.ownerId = ownerId;
    }
    public String getAccountType(){
        return accountType;
    }
    public void setAccountType(String accountType){
        this.accountType = accountType;
    }
    public String getCurrency() {
        return currency;
    }
    public  void setCurrency(String currency)
    {
        this.currency = currency;
    }
    public BigDecimal getBalance(){
        return balance;
    }
    public void setBalance(BigDecimal balance){
        this.balance = balance;
    }
    public  LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
}
