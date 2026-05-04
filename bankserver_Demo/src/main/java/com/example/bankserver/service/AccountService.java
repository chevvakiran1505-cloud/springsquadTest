package com.example.bankserver.service;

import com.example.bankserver.dto.AccountResponse;
import com.example.bankserver.dto.TransactionResponse;
import com.example.bankserver.exception.ResourceNotFoundException;
import com.example.bankserver.model.Account;
import com.example.bankserver.model.Transaction;
import com.example.bankserver.repository.AccountRepository;
import com.example.bankserver.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * GET /api/v1/accounts
     * Returns only the caller's accounts.
     */
    public List<AccountResponse> getAccounts(String userId) {
        return accountRepository.findByOwner_UserId(userId)
                .stream()
                .map(this::toAccountDto)
                .toList();
    }

    /**
     * GET /api/v1/accounts/{accountId}
     * Ownership enforced in query. If not found OR not owned → 404.
     */
    public AccountResponse getAccount(String accountId, String userId) {
        Account account = accountRepository
                .findByAccountIdAndOwner_UserId(accountId, userId)
                .orElseThrow(ResourceNotFoundException::new);

        return toAccountDto(account);
    }

    /**
     * GET /api/v1/accounts/{accountId}/transactions
     * Ownership enforced first, then fetch transactions ordered DESC.
     */
    public List<TransactionResponse> getTransactions(String accountId, String userId) {
        // Enforce ownership (404 if not yours)
        accountRepository
                .findByAccountIdAndOwner_UserId(accountId, userId)
                .orElseThrow(ResourceNotFoundException::new);

        return transactionRepository
                .findByAccount_AccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(this::toTransactionDto)
                .toList();
    }

    // ===================== MAPPERS =====================

    private AccountResponse toAccountDto(Account a) {
        return new AccountResponse(
                a.getAccountId(),
                a.getAccountType(),
                a.getCurrency(),
                a.getBalance(),
                a.getCreatedAt()
        );
    }

    private TransactionResponse toTransactionDto(Transaction t) {
        return new TransactionResponse(
                t.getTransactionId(),
                t.getAccount().getAccountId(),
                t.getType(),
                t.getAmount(),
                t.getStatus(),
                t.getCounterparty(),
                t.getTransferGroupId(),
                t.getDescription(),
                t.getCreatedAt()
        );
    }
}