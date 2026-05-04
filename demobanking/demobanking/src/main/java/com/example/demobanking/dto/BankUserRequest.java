package com.example.demobanking.dto;
import com.example.demobanking.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BankUserRequest {
    @NotBlank(message = "userId is required")
    @Size(max = 64, message =" userId must not exceed 64 characters")
    private String userId;
    @NotBlank(message = "subject is required")
    @Size(max = 255, message =" subject must not exceed 255 characters")
    private String subject;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 255, message ="email must  not exceed 255 characters")
    private String email;

    @Size(max = 255, message ="displayname must  not exceed 255 characters")
    private String displayName;
    @NotNull(message = "role is required")
    private UserRole role;

    public BankUserRequest()
    {

    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }
}
