package com.example.demobanking.controller;
import com.example.demobanking.dto.AccountCreateRequest;
import com.example.demobanking.dto.AccountResponse;
import com.example.demobanking.service.AccountService;
import com.example.demobanking.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankAccountService accountService;

    public BankAccountController(BankAccountService accountService){
        this.accountService = accountService;
    }
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreateRequest request)
    {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

   /* @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable String accountId)
    {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }*/
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<AccountResponse>> getAccountByOwnerId(@PathVariable String ownerId)
    {
        return ResponseEntity.ok(accountService.getAccountsByOwnerId(ownerId));
    }
}
