//package com.example.bankserver.service;
//
//import com.example.bankserver.config.PaymentProcessorClient;
//import com.example.bankserver.dto.TransactionRequest;
//import com.example.bankserver.dto.TransactionResponse;
//import com.example.bankserver.exception.InsufficientFundsException;
//import com.example.bankserver.exception.ResourceNotFoundException;
//import com.example.bankserver.model.Account;
//import com.example.bankserver.model.AccountType;
//import com.example.bankserver.model.Transaction;
//import com.example.bankserver.model.TransactionStatus;
//import com.example.bankserver.model.TransactionType;
//import com.example.bankserver.model.BankUser;
//import com.example.bankserver.repository.AccountRepository;
//import com.example.bankserver.repository.TransactionRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TransactionServiceUnitTest {
//
//    @Mock
//    private AccountRepository accountRepo;
//
//    @Mock
//    private TransactionRepository txnRepo;
//
//    @Mock
//    private PaymentProcessorClient paymentClient;
//
//    private TransactionService transactionService;
//
//    @BeforeEach
//    void setup() {
//        transactionService = new TransactionService(accountRepo, txnRepo, paymentClient);
//    }
//
//    @Test
//    void deposit_successful() {
//        BankUser user = new BankUser();
//        user.setId("user1");
//        user.setUserId("uid1");
//
//        Account account = new Account("acc_001", user, AccountType.CHECKING, "USD");
//        account.setBalance(new BigDecimal("100"));
//
//        TransactionRequest req = new TransactionRequest();
//        req.setAccountId("acc_001");
//        req.setType(TransactionType.DEPOSIT);
//        req.setAmount(new BigDecimal("50"));
//        req.setDescription("Test deposit");
//
//        when(accountRepo.findByAccountIdAndOwner_UserId("acc_001", "uid1"))
//                .thenReturn(Optional.of(account));
//        when(txnRepo.save(any(Transaction.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        TransactionResponse response = (TransactionResponse) transactionService.process("uid1", req);
//
//        assertNotNull(response);
//        assertEquals("acc_001", response.getAccountId());
//        assertEquals(TransactionType.DEPOSIT, response.getType());
//        assertEquals(new BigDecimal("50"), response.getAmount());
//        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
//
//        verify(txnRepo).save(any(Transaction.class));
//    }
//
//    @Test
//    void withdrawal_successful() {
//        BankUser user = new BankUser();
//        user.setId("user1");
//        user.setUserId("uid1");
//
//        Account account = new Account("acc_001", user, AccountType.CHECKING, "USD");
//        account.setBalance(new BigDecimal("100"));
//
//        TransactionRequest req = new TransactionRequest();
//        req.setAccountId("acc_001");
//        req.setType(TransactionType.WITHDRAWAL);
//        req.setAmount(new BigDecimal("40"));
//        req.setDescription("Test withdrawal");
//
//        when(accountRepo.findByAccountIdAndOwner_UserId("acc_001", "uid1"))
//                .thenReturn(Optional.of(account));
//        when(txnRepo.save(any(Transaction.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        TransactionResponse response = (TransactionResponse) transactionService.process("uid1", req);
//
//        assertNotNull(response);
//        assertEquals("acc_001", response.getAccountId());
//        assertEquals(TransactionType.WITHDRAWAL, response.getType());
//        assertEquals(new BigDecimal("40"), response.getAmount());
//        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
//
//        verify(txnRepo).save(any(Transaction.class));
//    }
//
//    @Test
//    void withdrawal_insufficientBalance_throws() {
//        BankUser user = new BankUser();
//        user.setId("user1");
//        user.setUserId("uid1");
//
//        Account account = new Account("acc_001", user, AccountType.CHECKING, "USD");
//        account.setBalance(new BigDecimal("10"));
//
//        TransactionRequest req = new TransactionRequest();
//        req.setAccountId("acc_001");
//        req.setType(TransactionType.WITHDRAWAL);
//        req.setAmount(new BigDecimal("40"));
//        req.setDescription("Test withdrawal");
//
//        when(accountRepo.findByAccountIdAndOwner_UserId("acc_001", "uid1"))
//                .thenReturn(Optional.of(account));
//
//        assertThrows(InsufficientFundsException.class,
//                () -> transactionService.process("uid1", req));
//
//        verify(txnRepo, never()).save(any());
//    }
//
//    @Test
//    void process_accountNotFound_throws() {
//        TransactionRequest req = new TransactionRequest();
//        req.setAccountId("acc_notfound");
//        req.setType(TransactionType.DEPOSIT);
//        req.setAmount(new BigDecimal("50"));
//
//        when(accountRepo.findByAccountIdAndOwner_UserId("acc_notfound", "uid1"))
//                .thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class,
//                () -> transactionService.process("uid1", req));
//
//        verify(txnRepo, never()).save(any());
//    }
//}