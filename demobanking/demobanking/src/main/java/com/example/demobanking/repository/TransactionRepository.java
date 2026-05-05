package com.example.demobanking.repository;
import com.example.demobanking.entity.AccountEntity;
import com.example.demobanking.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    List<TransactionEntity> findByAccountIdOrderByCreatedAtDesc(String accountId);
    List<TransactionEntity> findByTransferGroupId(String transferGroupId);

}
