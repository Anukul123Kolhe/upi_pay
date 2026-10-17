package com.simupi.dto.response;

import com.simupi.model.enums.AccountType;
import java.math.BigDecimal;

/**
 * Balance check response payload containing bank name, masked account number, and available balance.
 */
public class BalanceResponse {

    private String bankName;
    private String maskedAccountNumber;
    private AccountType accountType;
    private BigDecimal balance;

    public BalanceResponse() {}

    public BalanceResponse(String bankName, String maskedAccountNumber, AccountType accountType, BigDecimal balance) {
        this.bankName = bankName;
        this.maskedAccountNumber = maskedAccountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMaskedAccountNumber() {
        return maskedAccountNumber;
    }

    public void setMaskedAccountNumber(String maskedAccountNumber) {
        this.maskedAccountNumber = maskedAccountNumber;
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
}
