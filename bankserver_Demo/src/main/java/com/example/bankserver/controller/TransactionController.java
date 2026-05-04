package com.example.bankserver.controller;

import com.example.bankserver.dto.TransactionRequest;
import com.example.bankserver.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @PostMapping
    public ResponseEntity<?> createTransaction(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransactionRequest request) {

        Object result = service.process(jwt.getSubject(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}