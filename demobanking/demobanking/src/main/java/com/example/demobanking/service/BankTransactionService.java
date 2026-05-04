package com.example.demobanking.service;
import com.example.demobanking.dto.DepositRequest;
import com.example.demobanking.dto.TransactionResponse;
import com.example.demobanking.dto.TransferRequest;
import com.example.demobanking.dto.TransferResponse;
import com.example.demobanking.dto.WithdrawRequest;
import com.example.demobanking.entity.TransactionEntity;
import com.example.demobanking.entity.AccountEntity;
import com.example.demobanking.entity.TransactionStatus;
import com.example.demobanking.entity.TransactionType;
/*import com.example.demobanking.exception.BadRequestException;
import com.example.demobanking.exception.InsufficientFundsException;*/
import com.example.demobanking.exception.ResourceNotFoundException;
import com.example.demobanking.repository.AccountRepository;
import com.example.demobanking.repository.TransactionRepository;
//import com.example.demobanking.service.TransactionService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Service

public class BankTransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BankTransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        //validatePositiveAmount(request.getAmount());
        AccountEntity account = getAccountForUpdate(request.getAccountId());
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId(this.generateId());
        transaction.setAccountId(account.getAccountId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount((request.getAmount()));
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription(request.getDescription());

        TransactionEntity saved = transactionRepository.save(transaction);
        return new TransactionResponse(saved);
    }
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        //validatePositiveAmount(request.getAmount());
        AccountEntity account = getAccountForUpdate(request.getAccountId());
      /*  if(account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for accountId: " + request.getAccountId());
        }*/
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId(this.generateId());
        transaction.setAccountId(account.getAccountId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount((request.getAmount()));
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription(request.getDescription());

        TransactionEntity saved = transactionRepository.save(transaction);
        return new TransactionResponse(saved);
    }
    @Transactional
    public TransferResponse executeTransfer(TransferRequest request){
       /* validatePositiveAmount(request.getAmount());
        if(request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BadRequestException("fromAccountId and toAccountId must be different");
        }*/

        AccountEntity firstLock;
        AccountEntity secondLock;
        if(request.getfromAccountId().compareTo(request.gettoAccountId()) < 0) {
            firstLock = getAccountForUpdate(request.getfromAccountId());
            secondLock = getAccountForUpdate(request.gettoAccountId());
        } else {
            firstLock = getAccountForUpdate(request.gettoAccountId());
            secondLock = getAccountForUpdate(request.getfromAccountId());

        }
        AccountEntity fromAccount = firstLock.getAccountId().equals(request.getfromAccountId()) ? firstLock: secondLock;
        AccountEntity toAccount = firstLock.getAccountId().equals(request.gettoAccountId()) ? firstLock: secondLock;

        //validateTransferRules(fromAccount, toAccount, request.getAmount());
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        String transferGroupId = generateId();

        TransactionEntity debitTransaction = new TransactionEntity();
        debitTransaction.setTransactionId(generateId());
        debitTransaction.setAccountId(fromAccount.getAccountId());
        debitTransaction.setType(TransactionType.TRANSFER_OUT);
        debitTransaction.setAmount(request.getAmount());
        debitTransaction.setStatus(TransactionStatus.COMPLETED);
        debitTransaction.setCounterParty(toAccount.getAccountId());
        debitTransaction.setTransferGroupId(transferGroupId);
        debitTransaction.setDescription(request.getDescription());

        TransactionEntity creditTransaction = new TransactionEntity();
        debitTransaction.setTransactionId(generateId());
        debitTransaction.setAccountId(fromAccount.getAccountId());
        debitTransaction.setType(TransactionType.TRANSFER_IN);
        debitTransaction.setAmount(request.getAmount());
        debitTransaction.setStatus(TransactionStatus.COMPLETED);
        debitTransaction.setCounterParty(toAccount.getAccountId());
        debitTransaction.setTransferGroupId(transferGroupId);
        debitTransaction.setDescription(request.getDescription());

        TransactionEntity savedDebit = transactionRepository.save(debitTransaction);
        TransactionEntity savedCredit = transactionRepository.save(creditTransaction);

        return new TransferResponse(
                transferGroupId,
                new TransactionResponse(savedDebit),
                    new TransactionResponse(savedCredit),
                    fromAccount.getBalance(),
                    toAccount.getBalance()
        );
    }
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsBYAccountId(String accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByTransferGroupId(String transferGroupId) {
        return transactionRepository.findByTransferGroupId(transferGroupId)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }
    private  AccountEntity getAccountForUpdate(String accountId)   {
        return accountRepository.findByAccountIdForUpdate(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with accountId: " +accountId));
    }
  /*  private void validatePositiveAmount(BigDecimal amount) {
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("amount must be greated than 0");
        }
    }
    private void validateTransferRules(AccountEntity fromAccount, AccountEntity toAccount, BigDecimal amount){
        if(!fromAccount.getCurrency().equalsIgnoreCase(toAccount.getCurrency())) {
            throw new BadRequestException("Transfer between differenct currencies is not supported");
        }
        if(fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for accountId: " + request.getAccountId());
        }
    }*/
    private String generateId() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
