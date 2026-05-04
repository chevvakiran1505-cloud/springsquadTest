package com.example.demobanking.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
public class TransferRequest {
    @NotBlank(message = "fromAccountId is required")
    @Size(max = 64, message =" fromAccountId must not exceed 64 characters")
    private String fromAccountId;
    @NotBlank(message = "toAccountId is required")
    @Size(max = 64, message =" toAccountId must not exceed 64 characters")
    private String toAccountId;
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.0001", message ="amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 255, message ="description must  not exceed 255 characters")
    private String description;


    public TransferRequest() {

    }
    public String getfromAccountId()
    {
        return fromAccountId;
    }
    public void setfromAccountId(String FromAccountId) {
        this.fromAccountId = fromAccountId;
    }
    public String gettoAccountId()
    {
        return toAccountId;
    }
    public void settoAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
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
