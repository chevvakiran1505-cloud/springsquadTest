package com.example.bankbff.controller;

import com.example.bankbff.dto.UserInfoDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/me")
    public UserInfoDto me(@AuthenticationPrincipal OidcUser user) {
        String username = user.getPreferredUsername();
        if (username == null) {
            username = user.getSubject();
        }

        String email = user.getEmail();
        if (email == null) {
            email = username;
        }

        String name = user.getFullName();
        if (name == null) {
            name = username;
        }

        List<String> roles = user.getClaimAsStringList("roles");
        if (roles == null) {
            roles = List.of();
        }

        return new UserInfoDto(username, email, name, roles);
    }
}