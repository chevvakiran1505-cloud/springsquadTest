package com.example.demobanking.service;

import com.example.demobanking.model.BankUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final Map<String, BankUser> store = new ConcurrentHashMap<>();

    public UserService() {
        store.put("user-1", new BankUser("user-1", "alice", "alice@bank.example", "CUSTOMER"));
        store.put("user-2", new BankUser("user-2", "bob", "bob@bank.example", "CUSTOMER"));
        store.put("user-3", new BankUser("user-3", "carol", "carol@bank.example", "ADMIN"));
    }

    // TODO 27: Add @PreAuthorize to require SCOPE_admin:users.
    // This is the most sensitive endpoint. Only the full-access client holds this scope.
    @PreAuthorize("hasAuthority('SCOPE_admin:users')")
    public List<BankUser> findAll() {
        return List.copyOf(store.values());
    }

    // TODO 28: Add @PreAuthorize to require SCOPE_admin:users.
    @PreAuthorize("hasAuthority('SCOPE_admin:users')")
    public Optional<BankUser> findById(String userId) {
        return Optional.ofNullable(store.get(userId));
    }
}
