package com.example.demobanking.dto;
import com.example.demobanking.entity.UserRole;
import com.example.demobanking.entity.BankUser;
import java.time.LocalDateTime;
public class BankUserResponse {
    private String userId;
    private String subject;
    private String email;
    private String displayName;
    private UserRole role;
    private LocalDateTime createdAt;

    public BankUserResponse() {

    }

    public BankUserResponse(BankUser bankUser) {
       this.userId = bankUser.getUserId();
       this.subject = bankUser.getSubject();
       this.email = bankUser.getEmail();
       this.displayName = bankUser.getDisplayName();
       this.role = bankUser.getRole();
       this.createdAt = bankUser.getCreatedAt();
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
