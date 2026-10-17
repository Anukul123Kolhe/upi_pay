package com.simupi.exception;

import java.math.BigDecimal;

/**
 * Thrown when an attempted transaction would cause the user's cumulative daily transfers to exceed their account limit.
 */
public class DailyLimitExceededException extends SimUpiException {
    private final BigDecimal dailyLimit;
    private final BigDecimal alreadyTransferred;
    private final BigDecimal attemptedAmount;

    public DailyLimitExceededException(BigDecimal dailyLimit, BigDecimal alreadyTransferred, BigDecimal attemptedAmount) {
        super(String.format("Daily transfer limit exceeded. Daily limit: ₹%s, already transferred today: ₹%s, attempted transfer: ₹%s.",
                dailyLimit, alreadyTransferred, attemptedAmount));
        this.dailyLimit = dailyLimit;
        this.alreadyTransferred = alreadyTransferred;
        this.attemptedAmount = attemptedAmount;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public BigDecimal getAlreadyTransferred() {
        return alreadyTransferred;
    }

    public BigDecimal getAttemptedAmount() {
        return attemptedAmount;
    }
}
