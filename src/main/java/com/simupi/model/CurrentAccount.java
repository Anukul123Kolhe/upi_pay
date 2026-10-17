package com.simupi.model;

import com.simupi.exception.DailyLimitExceededException;
import com.simupi.exception.InsufficientBalanceException;
import com.simupi.model.enums.AccountType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

/**
 * OOP: Inheritance - CurrentAccount extends BankAccount.
 * Represents a commercial/business checking account with higher thresholds:
 * - Minimum Balance: ₹5,000.00
 * - Daily Transfer Limit: ₹2,00,000.00
 */
@Entity
@DiscriminatorValue("CURRENT")
public class CurrentAccount extends BankAccount {

    public static final BigDecimal DEFAULT_MIN_BALANCE = new BigDecimal("5000.00");
    public static final BigDecimal DEFAULT_DAILY_LIMIT = new BigDecimal("200000.00");

    public CurrentAccount() {
        super();
        setMinimumBalance(DEFAULT_MIN_BALANCE);
        setDailyTransferLimit(DEFAULT_DAILY_LIMIT);
    }

    public CurrentAccount(User user, String accountNumber, String bankName, String ifscCode, BigDecimal balance) {
        super(user, accountNumber, bankName, ifscCode, balance, DEFAULT_MIN_BALANCE, DEFAULT_DAILY_LIMIT);
    }

    // OOP: Polymorphism - Specialized commercial rules for current accounts
    @Override
    public void validateTransfer(BigDecimal amount, BigDecimal totalDebitedToday) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be strictly greater than zero.");
        }

        // 1. Maintain minimum operational liquidity threshold (₹5,000)
        BigDecimal postBalance = getBalance().subtract(amount);
        if (postBalance.compareTo(getMinimumBalance()) < 0) {
            throw new InsufficientBalanceException(getBalance(), amount, getMinimumBalance());
        }

        // 2. High commercial limit validation (₹2,00,000)
        BigDecimal prospectiveDailyTotal = (totalDebitedToday != null ? totalDebitedToday : BigDecimal.ZERO).add(amount);
        if (prospectiveDailyTotal.compareTo(getDailyTransferLimit()) > 0) {
            throw new DailyLimitExceededException(getDailyTransferLimit(), totalDebitedToday, amount);
        }
    }

    // OOP: Polymorphism - Overriding debit method for Current accounts
    @Override
    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be strictly positive.");
        }
        setBalanceInternal(getBalance().subtract(amount));
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.CURRENT;
    }
}
