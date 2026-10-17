package com.simupi.model;

import com.simupi.exception.DailyLimitExceededException;
import com.simupi.exception.InsufficientBalanceException;
import com.simupi.model.enums.AccountType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

/**
 * OOP: Inheritance - SavingsAccount extends BankAccount.
 * Represents an individual savings account with standard retail limits:
 * - Minimum Balance: ₹1,000.00
 * - Daily Transfer Limit: ₹1,00,000.00
 */
@Entity
@DiscriminatorValue("SAVINGS")
public class SavingsAccount extends BankAccount {

    public static final BigDecimal DEFAULT_MIN_BALANCE = new BigDecimal("1000.00");
    public static final BigDecimal DEFAULT_DAILY_LIMIT = new BigDecimal("100000.00");

    public SavingsAccount() {
        super();
        setMinimumBalance(DEFAULT_MIN_BALANCE);
        setDailyTransferLimit(DEFAULT_DAILY_LIMIT);
    }

    public SavingsAccount(User user, String accountNumber, String bankName, String ifscCode, BigDecimal balance) {
        super(user, accountNumber, bankName, ifscCode, balance, DEFAULT_MIN_BALANCE, DEFAULT_DAILY_LIMIT);
    }

    // OOP: Polymorphism - Overriding validation rules specifically tailored for savings accounts
    @Override
    public void validateTransfer(BigDecimal amount, BigDecimal totalDebitedToday) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be strictly greater than zero.");
        }

        // 1. Check if post-debit balance drops below the required minimum balance
        BigDecimal postBalance = getBalance().subtract(amount);
        if (postBalance.compareTo(getMinimumBalance()) < 0) {
            throw new InsufficientBalanceException(getBalance(), amount, getMinimumBalance());
        }

        // 2. Check cumulative daily limit compliance
        BigDecimal prospectiveDailyTotal = (totalDebitedToday != null ? totalDebitedToday : BigDecimal.ZERO).add(amount);
        if (prospectiveDailyTotal.compareTo(getDailyTransferLimit()) > 0) {
            throw new DailyLimitExceededException(getDailyTransferLimit(), totalDebitedToday, amount);
        }
    }

    // OOP: Polymorphism - Overriding atomic debit operation
    @Override
    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be strictly positive.");
        }
        setBalanceInternal(getBalance().subtract(amount));
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }
}
