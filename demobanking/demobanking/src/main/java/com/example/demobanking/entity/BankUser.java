package com.example.demobanking.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(
     name ="BANK_USERS"
    /* uniqueConstraints = {
             @UniqueConstraint(name ="UK_BANK_USERS_SUBJECT, ColumnNames = "SUBJECT")
     }   */

)
public class BankUser {
     @Id
     @Column(name ="USER_ID", nullable= false, length = 64)
                 private String userId;
    @Column(name ="SUBJECT", nullable= false, length = 255)
    private String subject;
    @Column(name ="EMAIL", nullable= false, length = 255)
    private String email;
    @Column(name ="DISPLAY_NAME",  length = 255)
    private String displayName;
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 16)
    private UserRole role;

    @Column(name ="CREATED_AT", nullable= false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public BankUser() {

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
