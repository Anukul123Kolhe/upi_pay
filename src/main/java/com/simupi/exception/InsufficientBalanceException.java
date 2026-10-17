package com.simupi.exception;

import java.math.BigDecimal;

/**
 * Thrown when an account does not have sufficient funds or violates minimum balance rules.
 */
public class InsufficientBalanceException extends SimUpiException {
    private final BigDecimal currentBalance;
    private final BigDecimal attemptedAmount;
    private final BigDecimal minimumBalance;

    public InsufficientBalanceException(String message) {
        super(message);
        this.currentBalance = BigDecimal.ZERO;
        this.attemptedAmount = BigDecimal.ZERO;
        this.minimumBalance = BigDecimal.ZERO;
    }

    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal attemptedAmount, BigDecimal minimumBalance) {
        super(String.format("Insufficient balance: Current balance is ₹%s, attempted debit is ₹%s, and minimum balance requirement is ₹%s.",
                currentBalance, attemptedAmount, minimumBalance));
        this.currentBalance = currentBalance;
        this.attemptedAmount = attemptedAmount;
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getAttemptedAmount() {
        return attemptedAmount;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }
}
