package com.example.bankserver.controller;

import com.example.bankserver.dto.UserResponse;
import com.example.bankserver.dto.AccountResponse;
import com.example.bankserver.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    // ✅ GET /api/v1/admin/users
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return service.getAllUsers();
    }

    // ✅ GET /api/v1/admin/users/{userId}/accounts
    @GetMapping("/users/{userId}/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountResponse> getUserAccounts(@PathVariable String userId) {
        return service.getAccountsForUser(userId);
    }
}