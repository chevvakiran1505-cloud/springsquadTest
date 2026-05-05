package com.example.demobanking.service;

import com.example.demobanking.dto.CreateTransactionRequest;
import com.example.demobanking.model.Transaction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionService {

    private final Map<String, Transaction> store = new ConcurrentHashMap<>();

    public TransactionService() {
        store.put("TXN-001", new Transaction("TXN-001", "ACC-001", "DEBIT", 200.00));
        store.put("TXN-002", new Transaction("TXN-002", "ACC-001", "CREDIT", 500.00));
        store.put("TXN-003", new Transaction("TXN-003", "ACC-002", "CREDIT", 1000.00));
    }

    // TODO 24: Add @PreAuthorize to require SCOPE_read:transactions.
    @PreAuthorize("hasAuthority('SCOPE_read:transactions')")
    public List<Transaction> findAll() {
        return List.copyOf(store.values());
    }

    // TODO 25: Add @PreAuthorize to require SCOPE_read:transactions.
    @PreAuthorize("hasAuthority('SCOPE_read:transactions')")
    public List<Transaction> findByAccountId(String accountId) {
        return store.values().stream()
                .filter(t -> t.accountId().equals(accountId))
                .toList();
    }

    // TODO 26: Add @PreAuthorize that requires SCOPE_write:transactions.
    @PreAuthorize("hasAuthority('SCOPE_write:transactions')")
    public Transaction create(CreateTransactionRequest request) {
        String newId = "TXN-" + (store.size() + 1);
        Transaction txn = new Transaction(newId, request.accountId(),
                request.type(), request.amount());
        store.put(newId, txn);
        return txn;
    }
}
