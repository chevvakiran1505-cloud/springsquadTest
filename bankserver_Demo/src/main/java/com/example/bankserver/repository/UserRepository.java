package com.example.bankserver.repository;

import com.example.bankserver.model.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<BankUser, String> {

    Optional<BankUser> findBySubject(String subject);
}