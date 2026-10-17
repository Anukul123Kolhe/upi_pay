package com.simupi.dto.response;

import com.simupi.model.enums.AccountType;
import java.math.BigDecimal;

/**
 * Bank account representation for UI display, masking account numbers for privacy.
 */
public class BankAccountResponse {

    private Long id;
    private String maskedAccountNumber;
    private String bankName;
    private String ifscCode;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal minimumBalance;
    private BigDecimal dailyTransferLimit;

    public BankAccountResponse() {}

    public BankAccountResponse(Long id, String maskedAccountNumber, String bankName, String ifscCode,
                               AccountType accountType, BigDecimal balance, BigDecimal minimumBalance, BigDecimal dailyTransferLimit) {
        this.id = id;
        this.maskedAccountNumber = maskedAccountNumber;
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.accountType = accountType;
        this.balance = balance;
        this.minimumBalance = minimumBalance;
        this.dailyTransferLimit = dailyTransferLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaskedAccountNumber() {
        return maskedAccountNumber;
    }

    public void setMaskedAccountNumber(String maskedAccountNumber) {
        this.maskedAccountNumber = maskedAccountNumber;
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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
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
}
