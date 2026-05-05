package com.example.demobanking.dto;
import com.example.demobanking.entity.AccountEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class AccountCreateRequest {
    @NotBlank(message = "accountId is required")
    @Size(max = 64, message =" accountId must not exceed 64 characters")
    private String accountId;
    @NotBlank(message = "ownerId is required")
    @Size(max = 64, message =" ownerId must not exceed 64 characters")
    private String ownerId;

    @NotBlank(message = "accountType is required")
    @Size(max = 16, message ="accountType must  not exceed 16 characters")
    private String accountType;

    @Size(max = 3, message ="currency must  not exceed 3 characters")
    private String currency;

    public AccountCreateRequest() {

    }
     public String getAccountId()
     {
         return accountId;
     }
     public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getOwnerId(){
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getAccountType() {
        return accountType = accountType;
    }
    public void setAccountType(String accountType)
    {
        this.accountType = accountType;

    }
    public String  getCurrency(){
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
