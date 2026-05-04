package com.example.bankserver.service;

import com.example.bankserver.dto.AccountResponse;
import com.example.bankserver.dto.UserResponse;
import com.example.bankserver.model.Account;
import com.example.bankserver.model.BankUser;
import com.example.bankserver.repository.AccountRepository;
import com.example.bankserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;

    // ===== USERS =====
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::toUserDto)
                .toList();
    }

    // ===== USER ACCOUNTS =====
    public List<AccountResponse> getAccountsForUser(String userId) {
        return accountRepo.findByOwner_UserId(userId)
                .stream()
                .map(this::toAccountDto)
                .toList();
    }

    // ===== MAPPERS =====
    private UserResponse toUserDto(BankUser u) {
        return new UserResponse(
                u.getUserId(),
                u.getEmail(),
                u.getDisplayName(),
                u.getRole()
        );
    }

    private AccountResponse toAccountDto(Account a) {
        return new AccountResponse(
                a.getAccountId(),
                a.getAccountType(),
                a.getCurrency(),
                a.getBalance(),
                a.getCreatedAt()
        );
    }
}