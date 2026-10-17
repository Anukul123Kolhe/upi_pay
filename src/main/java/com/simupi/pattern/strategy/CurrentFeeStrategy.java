package com.simupi.pattern.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy Implementation: Commercial current account transfers have a nominal 0.1% surcharge
 * for high-value business settlements (capped at ₹25.00).
 */
@Component("currentFeeStrategy")
public class CurrentFeeStrategy implements FeeCalculationStrategy {

    private static final BigDecimal FEE_RATE = new BigDecimal("0.001"); // 0.1%
    private static final BigDecimal MAX_FEE = new BigDecimal("25.00");
    private static final BigDecimal EXEMPTION_THRESHOLD = new BigDecimal("50000.00");

    @Override
    public BigDecimal calculateFee(BigDecimal amount) {
        if (amount == null || amount.compareTo(EXEMPTION_THRESHOLD) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal calculated = amount.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        return calculated.compareTo(MAX_FEE) > 0 ? MAX_FEE : calculated;
    }
}
