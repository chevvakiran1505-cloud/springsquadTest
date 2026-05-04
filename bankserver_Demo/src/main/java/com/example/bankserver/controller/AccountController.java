package com.example.bankserver.controller;

import com.example.bankserver.dto.AccountResponse;
import com.example.bankserver.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping
    public List<AccountResponse> getAccounts(@AuthenticationPrincipal Jwt jwt) {
        return service.getAccounts(jwt.getSubject());
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccount(
            @PathVariable String accountId,
            @AuthenticationPrincipal Jwt jwt) {

        return service.getAccount(accountId, jwt.getSubject());
    }

    @GetMapping("/{accountId}/transactions")
    public List<?> getTransactions(
            @PathVariable String accountId,
            @AuthenticationPrincipal Jwt jwt) {

        return service.getTransactions(accountId, jwt.getSubject());
    }
}