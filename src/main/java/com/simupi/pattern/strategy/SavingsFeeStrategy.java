package com.simupi.pattern.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Strategy Implementation: Savings accounts enjoy 100% zero-fee retail UPI transfers.
 */
@Component("savingsFeeStrategy")
public class SavingsFeeStrategy implements FeeCalculationStrategy {

    @Override
    public BigDecimal calculateFee(BigDecimal amount) {
        return BigDecimal.ZERO;
    }
}
