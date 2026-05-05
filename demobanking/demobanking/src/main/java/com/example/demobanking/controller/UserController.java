package com.example.demobanking.controller;

import com.example.demobanking.model.BankUser;
import com.example.demobanking.service.UserService;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private UserService userService;
    public  UserController( UserService userService)
    {
        this.userService = userService;
    }
    private static final List<BankUser> USERS = List.of(
            new BankUser("user-1", "alice", "alice@bank.example", "CUSTOMER"),
            new BankUser("user-2", "bob", "bob@bank.example", "CUSTOMER"),
            new BankUser("user-3", "carol", "carol@bank.example", "ADMIN")
    );

    @GetMapping
    public List<BankUser> getAllUsers() {
        return  userService.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BankUser> getUserById(@PathVariable String userId) {
        return USERS.stream()
                .filter(u -> u.userId().equals(userId))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
