package com.example.demobanking.controller;

import com.example.demobanking.model.Transaction;
import com.example.demobanking.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    private static final List<Transaction> TRANSACTIONS = List.of(
            new Transaction("TXN-001", "ACC-001", "DEBIT", 200.00),
            new Transaction("TXN-002", "ACC-001", "CREDIT", 500.00),
            new Transaction("TXN-003", "ACC-002", "CREDIT", 1000.00)
    );

    @GetMapping
    public List<Transaction> getAllTransactions() {
       // return TRANSACTIONS;
        return transactionService.findAll();
    }

    @GetMapping("/account/{accountId}")
    public List<Transaction> getTransactionsByAccount(@PathVariable String accountId) {
        return TRANSACTIONS.stream()
                .filter(t -> t.accountId().equals(accountId))
                .toList();
    }

    // TODO 2: Add a POST endpoint at the mapping root that accepts a request body
    // of type CreateTransactionRequest (you will create this DTO in Exercise 3).
    // Annotate the parameter with @RequestBody and @Valid.
    // Return 201 Created as a stub.
/*   @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody @Valid CreateTransactionRequest request) {
        Transaction created = transactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }*/
}
