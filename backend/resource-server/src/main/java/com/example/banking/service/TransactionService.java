package com.example.banking.service;

import com.example.banking.dto.NewTransactionRequest;
import com.example.banking.dto.TransactionDto;
import com.example.banking.exception.BusinessRuleException;
import com.example.banking.exception.InsufficientFundsException;
import com.example.banking.exception.PaymentProcessorException;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.kafka.TransactionEvent;
import com.example.banking.kafka.TransactionEventPublisher;
import com.example.banking.model.AccountEntity;
import com.example.banking.model.TransactionEntity;
import com.example.banking.model.TransactionStatus;
import com.example.banking.model.TransactionType;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class TransactionService {

    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    private final AccountService accountService;
    private final PaymentService paymentService;
    private final TransactionEventPublisher publisher;

    public TransactionService(AccountRepository accounts,
                              TransactionRepository transactions,
                              AccountService accountService,
                              PaymentService paymentService,
                              TransactionEventPublisher publisher) {
        this.accounts = accounts;
        this.transactions = transactions;
        this.accountService = accountService;
        this.paymentService = paymentService;
        this.publisher = publisher;
    }

    /**
     * All transactions on an account, newest first. Ownership enforced.
     */
    public List<TransactionDto> listForOwnedAccount(String accountId, String callerUserId) {
        accountService.loadOwned(accountId, callerUserId); // throws 404 if not owned
        return transactions.findByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(TransactionDto::from)
                .toList();
    }


    @Transactional
    public List<TransactionDto> submit(NewTransactionRequest req, String callerUserId) {
        TransactionType type = parseType(req.type());
        AccountEntity source = accountService.loadOwned(req.accountId(), callerUserId);
       return switch (type) {
            case DEPOSIT -> List.of(applyDeposit(source, req));
            case WITHDRAWAL -> List.of(applyWithdrawal(source, req));
            case TRANSFER_OUT -> applyTransferOut(source,req, callerUserId);
            case TRANSFER_IN -> throw new BusinessRuleException(
                    "TRANSFER_IN is created by the system; clients cannot post it directly");
        };

    }

    // ---- DEPOSIT --------------------------------------------------------


    private TransactionDto applyDeposit(AccountEntity source, NewTransactionRequest req) {
        // Rule 1: Deposit must NOT have a counterparty
        if (req.counterparty() != null && !req.counterparty().isBlank()) {
            throw new BusinessRuleException("DEPOSIT transactions must not have a counterparty");
        }

        // Rule 2: Add amount to balance
        source.setBalance(source.getBalance().add(req.amount()));

        // Rule 3: Persist the updated account
        accounts.save(source);

        // Rule 4 & 5: Create transaction row and return DTO
        TransactionEntity row = persistRow(
                source.getAccountId(),
                TransactionType.DEPOSIT,
                req.amount(),
                TransactionStatus.COMPLETED,
                null,          // counterparty
                null,           // transferGroupId
                req.description()
        );

        return TransactionDto.from(row);
    }

    // ---- WITHDRAWAL -----------------------------------------------------


    private TransactionDto applyWithdrawal(AccountEntity source, NewTransactionRequest req) {
        // Rule 1: Withdrawal must NOT have a counterparty
        if (req.counterparty() != null && !req.counterparty().isBlank()) {
            throw new BusinessRuleException("WITHDRAWAL transactions must not have a counterparty");
        }

        // Rule 2: Check sufficient funds BEFORE modifying balance
        requireFunds(source, req.amount());

        // Rule 3: Subtract amount from balance
        source.setBalance(source.getBalance().subtract(req.amount()));

        // Rule 4: Persist the updated account
        accounts.save(source);

        // Rule 5 & 6: Create transaction row and return DTO
        TransactionEntity row = persistRow(
                source.getAccountId(),
                TransactionType.WITHDRAWAL,
                req.amount(),
                TransactionStatus.COMPLETED,
                null,           // counterparty
                null,           // transferGroupId
                req.description()
        );

        return TransactionDto.from(row);
    }

    // ---- TRANSFER_OUT ---------------------------------------------------
    private List<TransactionDto> applyTransferOut(AccountEntity source, NewTransactionRequest req, String callerUserId) {
        // === Validation (both branches) ===
        // Rule 1: Counterparty must be non-null and non-blank
        if (req.counterparty() == null || req.counterparty().isBlank()) {
            throw new BusinessRuleException("TRANSFER_OUT requires a counterparty");
        }

        // Rule 2: Check sufficient funds BEFORE any modifications
        requireFunds(source, req.amount());

        // === Determine internal vs external ===
        // Rule 3: Load all accounts owned by the caller
        List<AccountEntity> ownedAccounts = accounts.findByOwnerId(callerUserId);

        // Rule 4: Check if counterparty is an internal account (owned by caller)
        boolean isInternalTransfer = ownedAccounts.stream()
                .anyMatch(acc -> acc.getAccountId().equals(req.counterparty()));

        if (isInternalTransfer) {
            return handleInternalTransfer(source, req, ownedAccounts);
        } else {
            return handleExternalTransfer(source, req);
        }
    }

    /**
     * Internal transfer: both source and destination are owned by the same user.
     * Creates two linked transaction rows (TRANSFER_OUT and TRANSFER_IN).
     */
    private List<TransactionDto> handleInternalTransfer(AccountEntity source, NewTransactionRequest req, List<AccountEntity> ownedAccounts) {
        // Rule 5: Find the destination account from the owned list
        AccountEntity destination = ownedAccounts.stream()
                .filter(acc -> acc.getAccountId().equals(req.counterparty()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("account", req.counterparty()));

        // Rule 6: Generate a shared transferGroupId
        String transferGroupId = "grp_" + UUID.randomUUID();

        // Rule 7: Debit source (subtract amount, save)
        source.setBalance(source.getBalance().subtract(req.amount()));
        accounts.save(source);

        // Rule 8: Create TRANSFER_OUT row on source account
        TransactionEntity outRow = persistRow(
                source.getAccountId(),
                TransactionType.TRANSFER_OUT,
                req.amount(),
                TransactionStatus.COMPLETED,
                destination.getAccountId(),  // counterparty
                transferGroupId,
                req.description()
        );

        // Rule 9: Credit destination (add amount, save)
        destination.setBalance(destination.getBalance().add(req.amount()));
        accounts.save(destination);

        // Rule 10: Create TRANSFER_IN row on destination account
        TransactionEntity inRow = persistRow(
                destination.getAccountId(),
                TransactionType.TRANSFER_IN,
                req.amount(),
                TransactionStatus.COMPLETED,
                source.getAccountId(),  // counterparty
                transferGroupId,
                req.description()
        );

        // Rule 11: Return both rows
        return List.of(
                TransactionDto.from(outRow),
                TransactionDto.from(inRow)
        );
    }

    /**
     * External transfer: destination is outside the caller's owned accounts.
     * Calls PaymentService (WireMock). On success, debits source and creates TRANSFER_OUT row.
     * On failure, does NOT debit source, creates FAILED row, and rethrows exception.
     */
    private List<TransactionDto> handleExternalTransfer(AccountEntity source,
                                                        NewTransactionRequest req) {
        // Rule 12: Generate an idempotency key for the payment processor
        String idempotencyKey = UUID.randomUUID().toString();

        try {
            // Rule 13: Call paymentService.submitExternalTransfer (calls WireMock)
            paymentService.submitExternalTransfer(
                    source.getAccountId(),
                    req.counterparty(),
                    req.amount(),
                    source.getCurrency(),
                    idempotencyKey
            );

            // On SUCCESS: Debit source and persist TRANSFER_OUT row with COMPLETED status
            // Debit source (subtract amount, save)
            source.setBalance(source.getBalance().subtract(req.amount()));
            accounts.save(source);

            // Persist TRANSFER_OUT row, status = COMPLETED, transferGroupId = null
            TransactionEntity row = persistRow(
                    source.getAccountId(),
                    TransactionType.TRANSFER_OUT,
                    req.amount(),
                    TransactionStatus.COMPLETED,
                    req.counterparty(),
                    null,  // transferGroupId
                    req.description()
            );

            // Return list of one row
            return List.of(TransactionDto.from(row));

        } catch (PaymentProcessorException e) {
            // On FAILURE: Do NOT debit source, persist FAILED row, rethrow
            // Create a FAILED transaction row (without debiting the account)
            TransactionEntity failedRow = persistRow(
                    source.getAccountId(),
                    TransactionType.TRANSFER_OUT,
                    req.amount(),
                    TransactionStatus.FAILED,
                    req.counterparty(),
                    null,  // transferGroupId
                    req.description()
            );

            // Rethrow the exception so GlobalExceptionHandler maps it to 502
            throw e;
        }
    }



    // ---- helpers --------------------------------------------------------

    private void requireFunds(AccountEntity source, BigDecimal amount) {
        BigDecimal balance = source.getBalance();
         if (balance == null || balance.compareTo(amount) < 0) {

            throw new InsufficientFundsException(source.getAccountId(),
                    balance, amount);
        }

    }

    private TransactionEntity persistRow(String accountId, TransactionType type,
                                         BigDecimal amount, TransactionStatus status,
                                         String counterparty, String transferGroupId,
                                         String description) {
        TransactionEntity row = new TransactionEntity(
                "txn_" + UUID.randomUUID(),
                accountId,
                type,
                amount,
                status,
                counterparty,
                transferGroupId,
                description,
                LocalDateTime.now()
        );
        return transactions.save(row);
    }

    private TransactionType parseType(String raw) {
        try {
            return TransactionType.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Unknown transaction type: " + raw);
        }
    }

    /** Convenience for the controller — builds the event for an account it just acted on. */
    public TransactionEvent toEvent(TransactionDto tx, String ownerId, String currency) {
        // tx is a DTO; reconstruct just enough state to build an event payload
        return new TransactionEvent(
                "evt_" + UUID.randomUUID(),
                tx.transactionId(),
                tx.accountId(),
                ownerId,
                tx.type(),
                tx.amount(),
                currency,
                tx.status(),
                tx.counterparty(),
                tx.transferGroupId(),
                Instant.now()
        );
    }

    public void publishEvent(TransactionEvent event) {
        publisher.publish(event);
    }

    public TransactionDto findOwnedTransaction(String transactionId, String callerUserId) {
        TransactionEntity row = transactions.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("transaction", transactionId));
        accountService.loadOwned(row.getAccountId(), callerUserId); // 404 if not owned
        return TransactionDto.from(row);
    }
}
