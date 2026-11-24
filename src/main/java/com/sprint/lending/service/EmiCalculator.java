package com.sprint.lending.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class EmiCalculator {

    private static final BigDecimal MONTHS_PER_YEAR = BigDecimal.valueOf(12);
    private static final MathContext CALCULATION_CONTEXT = new MathContext(24, RoundingMode.HALF_UP);
    private static final int MONEY_SCALE = 2;

    /**
     * Calculates a monthly EMI from a nominal annual rate using the reducing-balance formula.
     * The annual rate is expressed as a decimal fraction (for example, 0.18 for 18%).
     */
    public BigDecimal calculate(BigDecimal principal, BigDecimal annualInterestRate, int tenureMonths) {
        if (principal == null || principal.signum() <= 0) {
            throw new IllegalArgumentException("Principal must be greater than zero.");
        }
        if (annualInterestRate == null || annualInterestRate.signum() < 0) {
            throw new IllegalArgumentException("Annual interest rate must not be negative.");
        }
        if (tenureMonths <= 0) {
            throw new IllegalArgumentException("Tenure must be at least one month.");
        }

        BigDecimal monthlyRate = annualInterestRate.divide(MONTHS_PER_YEAR, CALCULATION_CONTEXT);
        if (monthlyRate.signum() == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), MONEY_SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal growthFactor = BigDecimal.ONE.add(monthlyRate).pow(tenureMonths, CALCULATION_CONTEXT);
        BigDecimal numerator = principal.multiply(monthlyRate, CALCULATION_CONTEXT)
                .multiply(growthFactor, CALCULATION_CONTEXT);
        BigDecimal denominator = growthFactor.subtract(BigDecimal.ONE, CALCULATION_CONTEXT);

        return numerator.divide(denominator, CALCULATION_CONTEXT)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
