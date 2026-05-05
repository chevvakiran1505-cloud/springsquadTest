package com.example.demobanking.repository;
import com.example.demobanking.entity.AccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import  java.util.List;
import  java.util.Optional;
public interface AccountRepository extends JpaRepository<AccountEntity, String>{
    List<AccountEntity> findByOwnerId(String ownerId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a where a.accountId = :accountId")
    Optional<AccountEntity> findByAccountIdForUpdate(@Param("accountId") String accountId);

}
