package com.example.bankserver.repository;

import com.example.bankserver.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByOwner_UserId(String userId);

    Optional<Account> findByAccountIdAndOwner_UserId(String accountId, String userId);
}