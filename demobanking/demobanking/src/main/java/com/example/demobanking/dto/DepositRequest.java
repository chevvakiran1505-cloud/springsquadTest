package com.example.demobanking.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
public class DepositRequest {
    @NotBlank(message = "accountId is required")
    @Size(max = 64, message =" accountId must not exceed 64 characters")
    private String accountId;
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.0001", message ="amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "accountType is required")
    @Size(max = 255, message ="description must  not exceed 255 characters")
    private String description;


    public DepositRequest() {

    }
    public String getAccountId()
    {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;

    }

}
