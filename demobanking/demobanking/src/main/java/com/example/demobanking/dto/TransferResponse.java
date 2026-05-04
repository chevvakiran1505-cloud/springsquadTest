package com.example.demobanking.dto;
import java.math.BigDecimal;
public class TransferResponse {
    private  String transferGroupId;
    private TransactionResponse debitTransaction;
    private  TransactionResponse creditTransaction;
    private  BigDecimal fromAccountBalance;
    private BigDecimal toAccountBalance;
    public TransferResponse() {}
     public TransferResponse(String transferGroupId, TransactionResponse debitTransaction,
                TransactionResponse creditTransaction,
                BigDecimal fromAccountBalance,
                BigDecimal toAccountBalance)
     {
         this.transferGroupId = transferGroupId;
         this.debitTransaction = debitTransaction;
         this.creditTransaction = creditTransaction;
         this.fromAccountBalance = fromAccountBalance;
         this.toAccountBalance = toAccountBalance;
     }


        public  String getTransferGroupId(String transferGroupId) {
           return  transferGroupId;
        }

    public void setTransferGroupId(String transferGroupId) {
        this.transferGroupId = transferGroupId;
    }
    public TransactionResponse getDebitTransaction() {
        return  debitTransaction;
    }
    public void setDebitTransaction(TransactionResponse debitTransaction){
        this.debitTransaction = debitTransaction;
    }
    public  TransactionResponse getCreditTransaction(){
        return  creditTransaction;
    }
    public  void setCreditTransaction(TransactionResponse creditTransaction){
        this.creditTransaction = creditTransaction;

    }
    public  BigDecimal getFromAccountBalance() {
        return  fromAccountBalance;
    }
    public  void setFromAccountBalance(BigDecimal fromAccountBalance){
        this.fromAccountBalance = fromAccountBalance;
    }
    public  BigDecimal getToAccountBalance() {
        return toAccountBalance;
    }
    public  void  setToAccountBalance(BigDecimal toAccountBalance) {
        this.toAccountBalance = toAccountBalance;
    }
}
