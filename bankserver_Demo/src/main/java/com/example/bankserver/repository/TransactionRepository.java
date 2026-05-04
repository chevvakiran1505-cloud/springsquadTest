package com.example.bankserver.repository;

import com.example.bankserver.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByAccount_AccountIdOrderByCreatedAtDesc(String accountId);
}
