package com.simupi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User Entity representing a registered customer in the UPI network.
 * Demonstrates OOP: Encapsulation with private state and controlled business methods.
 */
@Entity
@Table(name = "users")
public class User {

    // OOP: Encapsulation - All fields are private, restricting direct external access
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String mobile;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "upi_id", nullable = false, unique = true, length = 50)
    private String upiId;

    @JsonIgnore
    @Column(name = "upi_pin_hash", nullable = false)
    private String upiPinHash;

    @Column(name = "failed_pin_attempts", nullable = false)
    private int failedPinAttempts = 0;

    @Column(name = "is_pin_locked", nullable = false)
    private boolean isPinLocked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BankAccount> accounts = new ArrayList<>();

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String name, String mobile, String email, String passwordHash, String upiId, String upiPinHash) {
        this();
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.passwordHash = passwordHash;
        this.upiId = upiId;
        this.upiPinHash = upiPinHash;
    }

    // OOP: Encapsulation - Domain logic encapsulated within the entity to maintain consistent state
    public void recordFailedPinAttempt() {
        this.failedPinAttempts++;
        if (this.failedPinAttempts >= 3) {
            this.isPinLocked = true;
        }
    }

    public void resetPinAttempts() {
        this.failedPinAttempts = 0;
        this.isPinLocked = false;
    }

    public void unlockPin(String newHashedPin) {
        this.upiPinHash = newHashedPin;
        this.failedPinAttempts = 0;
        this.isPinLocked = false;
    }

    public void addAccount(BankAccount account) {
        accounts.add(account);
        account.setUser(this);
    }

    // Getters and Setters (Encapsulation)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getUpiPinHash() {
        return upiPinHash;
    }

    public void setUpiPinHash(String upiPinHash) {
        this.upiPinHash = upiPinHash;
    }

    public int getFailedPinAttempts() {
        return failedPinAttempts;
    }

    public void setFailedPinAttempts(int failedPinAttempts) {
        this.failedPinAttempts = failedPinAttempts;
    }

    public boolean isPinLocked() {
        return isPinLocked;
    }

    public void setPinLocked(boolean pinLocked) {
        isPinLocked = pinLocked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BankAccount> accounts) {
        this.accounts = accounts;
    }
}
