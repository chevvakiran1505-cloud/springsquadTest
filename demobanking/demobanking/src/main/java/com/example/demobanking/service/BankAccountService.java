package com.example.demobanking.service;
import com.example.demobanking.dto.AccountCreateRequest;
import com.example.demobanking.dto.AccountResponse;
import com.example.demobanking.dto.BankUserRequest;
import com.example.demobanking.dto.BankUserResponse;
import com.example.demobanking.entity.AccountEntity;
import com.example.demobanking.entity.BankUser;
import com.example.demobanking.exception.DuplicateResourceException;
import com.example.demobanking.exception.ResourceNotFoundException;
import com.example.demobanking.repository.AccountRepository;
import com.example.demobanking.repository.BankUserRepository;
//import com.example.demobanking.service.BankUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
@Service
@Transactional
public class BankAccountService {
    private final AccountRepository accountRepository;
    private final BankUserRepository bankUserRepository;

    public BankAccountService(AccountRepository accountRepository, BankUserRepository bankUserRepository){
        this.accountRepository =accountRepository;
        this.bankUserRepository =bankUserRepository;
    }
    public AccountResponse createAccount(AccountCreateRequest request) {
        if(accountRepository.existsById(request.getAccountId())) {
            throw new DuplicateResourceException(("Account already exists with accountId:" + request.getAccountId()));

        }
        if(!bankUserRepository.existsById(request.getOwnerId())) {
            throw new ResourceNotFoundException(("Bankuser not found with userId: "+ request.getOwnerId()));
        }
        AccountEntity entity = new AccountEntity();
        entity.setAccountId(request.getAccountId());
        entity.setOwnerId(request.getOwnerId());
        entity.setCurrency(resolveCurrency(request.getCurrency()));
        entity.setBalance((BigDecimal.ZERO));
        AccountEntity saved = accountRepository.save(entity);
        return new AccountResponse(saved);

    }
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByOwnerId(String ownerId) {
        return accountRepository.findByOwnerId(ownerId)
                       .stream()
                .map(AccountResponse::new)
                .toList();
    }
    private AccountEntity getAccountEntity(String accountId){

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with accountId: " +accountId));
    }
    private String resolveCurrency(String currency) {
           if(currency == null || currency.isBlank()) {
                return "USD";
            }
            return currency.trim().toUpperCase();
        }
    }

