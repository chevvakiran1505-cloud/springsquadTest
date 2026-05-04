package com.example.bankserver.service;

import com.example.bankserver.config.PaymentProcessorClient;
import com.example.bankserver.dto.TransactionRequest;
import com.example.bankserver.dto.TransactionResponse;
import com.example.bankserver.exception.*;
import com.example.bankserver.model.*;
import com.example.bankserver.repository.AccountRepository;
import com.example.bankserver.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepo;
    private final TransactionRepository txnRepo;
    private final PaymentProcessorClient paymentClient;

    // ================= ENTRY =================

    @Transactional
    public Object process(String userId, TransactionRequest req) {

        Account source = accountRepo
                .findByAccountIdAndOwner_UserId(req.getAccountId(), userId)
                .orElseThrow(ResourceNotFoundException::new);

        return switch (req.getType()) {
            case DEPOSIT -> deposit(source, req);
            case WITHDRAWAL -> withdraw(source, req);
            case TRANSFER_OUT -> handleTransfer(source, req);
        };
    }

    // ================= DEPOSIT =================

    private TransactionResponse deposit(Account acc, TransactionRequest req) {

        acc.setBalance(acc.getBalance().add(req.getAmount()));

        Transaction txn = buildTxn(acc, req, TransactionType.DEPOSIT,
                TransactionStatus.COMPLETED, null, null);

        txnRepo.save(txn);

        return toDto(txn);
    }

    // ================= WITHDRAW =================

    private TransactionResponse withdraw(Account acc, TransactionRequest req) {

        if (acc.getBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientFundsException();
        }

        acc.setBalance(acc.getBalance().subtract(req.getAmount()));

        Transaction txn = buildTxn(acc, req, TransactionType.WITHDRAWAL,
                TransactionStatus.COMPLETED, null, null);

        txnRepo.save(txn);

        return toDto(txn);
    }

    // ================= TRANSFER =================

    private Object handleTransfer(Account source, TransactionRequest req) {

        if (req.getCounterparty() == null) {
            throw new BusinessRuleException("Counterparty required");
        }

        // INTERNAL TRANSFER
        if (req.getCounterparty().startsWith("acc_")) {
            return internalTransfer(source, req);
        }

        // EXTERNAL TRANSFER
        return externalTransfer(source, req);
    }

    // ================= INTERNAL =================

    private List<TransactionResponse> internalTransfer(Account source, TransactionRequest req) {

        Account target = accountRepo.findById(req.getCounterparty())
                .orElseThrow(ResourceNotFoundException::new);

        if (source.getBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientFundsException();
        }

        String groupId = "grp_" + UUID.randomUUID();

        // debit source
        source.setBalance(source.getBalance().subtract(req.getAmount()));

        Transaction out = buildTxn(
                source, req,
                TransactionType.TRANSFER_OUT,
                TransactionStatus.COMPLETED,
                target.getAccountId(),
                groupId
        );

        // credit target
        target.setBalance(target.getBalance().add(req.getAmount()));

        Transaction in = buildTxn(
                target, req,
                TransactionType.TRANSFER_IN,
                TransactionStatus.COMPLETED,
                source.getAccountId(),
                groupId
        );

        txnRepo.saveAll(List.of(out, in));

        return List.of(toDto(out), toDto(in));
    }

    // ================= EXTERNAL =================

    private TransactionResponse externalTransfer(Account source, TransactionRequest req) {

        // DO NOT debit yet
        try {
            paymentClient.processPayment(req);

            if (source.getBalance().compareTo(req.getAmount()) < 0) {
                throw new InsufficientFundsException();
            }

            source.setBalance(source.getBalance().subtract(req.getAmount()));

            Transaction txn = buildTxn(
                    source, req,
                    TransactionType.TRANSFER_OUT,
                    TransactionStatus.COMPLETED,
                    req.getCounterparty(),
                    null
            );

            txnRepo.save(txn);
            return toDto(txn);

        } catch (Exception ex) {

            Transaction failed = buildTxn(
                    source, req,
                    TransactionType.TRANSFER_OUT,
                    TransactionStatus.FAILED,
                    req.getCounterparty(),
                    null
            );

            txnRepo.save(failed);

            throw new PaymentProcessorException("External transfer failed");
        }
    }

    // ================= HELPERS =================

    private Transaction buildTxn(Account acc,
                                 TransactionRequest req,
                                 TransactionType type,
                                 TransactionStatus status,
                                 String counterparty,
                                 String groupId) {

        Transaction txn = new Transaction();
        txn.setTransactionId("txn_" + UUID.randomUUID());
        txn.setAccount(acc);
        txn.setType(type);
        txn.setAmount(req.getAmount());
        txn.setStatus(status);
        txn.setCounterparty(counterparty);
        txn.setTransferGroupId(groupId);
        txn.setDescription(req.getDescription());
        txn.setCreatedAt(Instant.now());

        return txn;
    }

    private TransactionResponse toDto(Transaction t) {
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