package com.example.bankserver.service;

import com.example.bankserver.dto.UserResponse;
import com.example.bankserver.model.BankUser;
import com.example.bankserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    public UserResponse getOrCreate(Jwt jwt) {

        String sub = jwt.getSubject();

        BankUser user = repo.findBySubject(sub)
                .orElseGet(() -> {
                    BankUser u = new BankUser();
                    u.setUserId("usr_" + UUID.randomUUID());
                    u.setSubject(sub);
                    u.setEmail(jwt.getClaim("email"));
                    u.setDisplayName(jwt.getClaim("name"));
                    u.setRole("CUSTOMER");
                    u.setCreatedAt(Instant.now());
                    return repo.save(u);
                });

        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole()
        );
    }
}