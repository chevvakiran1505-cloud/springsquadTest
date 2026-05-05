package com.example.demobanking.service;

import com.example.demobanking.dto.CreateAccountRequest;
import com.example.demobanking.model.Account;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {

    private final Map<String, Account> store = new ConcurrentHashMap<>();

    public AccountService() {
        store.put("ACC-001", new Account("ACC-001", "user-1", "CHECKING", 1500.00));
        store.put("ACC-002", new Account("ACC-002", "user-2", "SAVINGS", 8200.50));
        store.put("ACC-003", new Account("ACC-003", "user-1", "SAVINGS", 3100.75));
    }

    // TODO 20: Add @PreAuthorize to require SCOPE_read:accounts.
    // Any caller without this scope must be denied before the method body runs.
    @PreAuthorize("hasAuthority('SCOPE_read:accounts')")
    public List<Account> findAll() {
        return List.copyOf(store.values());
    }

    // TODO 21: Add @PreAuthorize to require SCOPE_read:accounts.
    //
    // TODO 22: Add @PostAuthorize that runs after the method returns.
    // The expression should enforce that the caller can only see accounts
    // they own, unless they also hold SCOPE_admin:users.
    // Expression: "returnObject.isEmpty() ||
    //              returnObject.get().ownerId() == authentication.name ||
    //              hasAuthority('SCOPE_admin:users')"
    //
    // @PostAuthorize is evaluated with the return value available as returnObject.
    // The method body runs first. If the expression is false, AccessDeniedException
    // is thrown and the return value is discarded. This enforces ownership at the
    // object level, not just at the URL level.
    @PreAuthorize("hasAuthority('SCOPE_read:accounts')")
    @PostAuthorize("returnObject.isEmpty() || " +
            "returnObject.get().ownerId() == authentication.name || " +
            "hasAuthority('SCOPE_admin:users')")
    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(store.get(accountId));
    }

    // TODO 23: Add @PreAuthorize that requires BOTH:
    //   hasAuthority('SCOPE_write:accounts') AND hasAuthority('SCOPE_read:accounts')
    // Combine them with &&.
    // A client with write scope but no read scope should be denied.
    @PreAuthorize("hasAuthority('SCOPE_write:accounts') && hasAuthority('SCOPE_read:accounts')")
    public Account create(CreateAccountRequest request) {
        String newId = "ACC-" + (store.size() + 1);
        Account account = new Account(newId, request.ownerId(),
                request.accountType(), request.initialDeposit());
        store.put(newId, account);
        return account;
    }
}
