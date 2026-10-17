package com.simupi.pattern.strategy;

import java.math.BigDecimal;

/**
 * Design Pattern: Strategy Pattern.
 * Defines a family of interchangeable fee calculation algorithms for financial transactions.
 */
public interface FeeCalculationStrategy {

    /**
     * Calculates the applicable fee/charge for the given transfer amount.
     *
     * @param amount The transaction amount
     * @return The charge in INR
     */
    BigDecimal calculateFee(BigDecimal amount);
}
