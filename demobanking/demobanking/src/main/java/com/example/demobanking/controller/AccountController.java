package com.example.demobanking.controller;

import com.example.demobanking.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demobanking.service.AccountService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.findAll();
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable String accountId) {
        return accountService.findById(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // TODO 1: Add a POST endpoint that accepts a request body and returns 201 Created.
    // The request body type will be CreateAccountRequest (you will create this DTO in Exercise 3).
    // Annotate the parameter with @RequestBody and @Valid.
    // Use ResponseEntity.status(HttpStatus.CREATED).body(request) as a stub return value.
    // Leave a comment noting that persistence is not implemented.
/*    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestBody @Valid CreateAccountRequest request) {
        Account created = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }*/
}
