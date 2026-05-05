package com.example.demobanking.controller;
import com.example.demobanking.dto.*;
import com.example.demobanking.service.BankTransactionService;
import com.example.demobanking.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/transactions")
public class BankTransactionController {
    private final BankTransactionService transactionService;
    public BankTransactionController(BankTransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(request));
    }
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest request)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.withdraw(request));
    }
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.executeTransfer(request));
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionByAccountId(@PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsBYAccountId(accountId));
    }
    @GetMapping("/group/{transferGroupId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionByTransferGroupId(@PathVariable String transferGroupId) {
        return ResponseEntity.ok(transactionService.getTransactionsByTransferGroupId(transferGroupId));
    }
}

