package com.example.demobanking.controller;
import com.example.demobanking.dto.BankUserRequest;
import com.example.demobanking.dto.BankUserResponse;
import com.example.demobanking.service.BankUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/bank-users")
public class BankUserController {
    private final BankUserService bankUserService;

    private BankUserController(BankUserService bankUserService) {
        this.bankUserService = bankUserService;

    }
    @PostMapping
    public ResponseEntity<BankUserResponse> createUser(@Valid @RequestBody BankUserRequest request){
        BankUserResponse response = bankUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<BankUserResponse> getUserById(@PathVariable String userId) {
        BankUserResponse response = bankUserService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BankUserResponse>> getAllUsers() {
        List<BankUserResponse> response = bankUserService.getAllUsers();
        return ResponseEntity.ok(response);
    }

}
