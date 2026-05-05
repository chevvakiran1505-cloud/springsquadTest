package com.example.demobanking.repository;

import org.springframework.stereotype.Repository;
import com.example.demobanking.entity.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
@Repository
public interface BankUserRepository extends JpaRepository<BankUser, String> {
    Optional<BankUser> findBySubject(String subject);
    boolean existsBySubject(String subject);
    boolean existsBySubjectAndUserIdNot(String subject, String userId);

}
