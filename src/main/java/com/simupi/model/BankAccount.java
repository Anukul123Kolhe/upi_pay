package com.simupi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simupi.model.enums.AccountType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OOP: Abstraction - Abstract base class representing a generic bank account.
 * High-level payment services interact with this abstraction without needing
 * to know specific account-type details (e.g., Savings vs Current).
 */
@Entity
@Table(name = "bank_accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
public abstract class BankAccount {

    // OOP: Encapsulation - Private fields protected from direct external tampering
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "account_number", nullable = false, unique = true, length = 16)
    private String accountNumber;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "ifsc_code", nullable = false, length = 11)
    private String ifscCode;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "minimum_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal minimumBalance;

    @Column(name = "daily_transfer_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal dailyTransferLimit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BankAccount() {
        this.createdAt = LocalDateTime.now();
        this.balance = BigDecimal.ZERO;
    }

    public BankAccount(User user, String accountNumber, String bankName, String ifscCode,
                       BigDecimal balance, BigDecimal minimumBalance, BigDecimal dailyTransferLimit) {
        this();
        this.user = user;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.minimumBalance = minimumBalance;
        this.dailyTransferLimit = dailyTransferLimit;
    }

    // OOP: Abstraction - Abstract methods enforcing specialized behavioral contracts in subclasses
    public abstract void validateTransfer(BigDecimal amount, BigDecimal totalDebitedToday);

    public abstract void debit(BigDecimal amount);

    public abstract AccountType getAccountType();

    // Common concrete business logic shared across all account types
    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be strictly positive.");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Helper to mask sensitive account number: returns e.g. "XXXX1234"
     */
    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "XXXX0000";
        }
        return "XXXX" + accountNumber.substring(accountNumber.length() - 4);
    }

    // Getters and Setters (OOP: Encapsulation)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    protected void setBalanceInternal(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getDailyTransferLimit() {
        return dailyTransferLimit;
    }

    public void setDailyTransferLimit(BigDecimal dailyTransferLimit) {
        this.dailyTransferLimit = dailyTransferLimit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
