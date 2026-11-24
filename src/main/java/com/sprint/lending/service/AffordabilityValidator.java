package com.sprint.lending.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class AffordabilityValidator {

    private final BigDecimal maximumEmiToIncomeRatio;

    public AffordabilityValidator(
            @Value("${app.loan.maximum-emi-to-income-ratio}") BigDecimal maximumEmiToIncomeRatio
    ) {
        this.maximumEmiToIncomeRatio = maximumEmiToIncomeRatio;
    }

    public void validate(BigDecimal emi, BigDecimal monthlyIncome) {
        if (emi == null || emi.signum() <= 0) {
            throw new IllegalArgumentException("EMI must be greater than zero.");
        }
        if (monthlyIncome == null || monthlyIncome.signum() <= 0) {
            throw new IllegalArgumentException("Monthly income must be greater than zero.");
        }

        BigDecimal maximumAffordableEmi = monthlyIncome.multiply(maximumEmiToIncomeRatio);
        if (emi.compareTo(maximumAffordableEmi) > 0) {
            BigDecimal ratioPercent = maximumEmiToIncomeRatio
                    .multiply(BigDecimal.valueOf(100))
                    .stripTrailingZeros();
            throw new IllegalArgumentException(
                    "EMI must not exceed " + ratioPercent.toPlainString()
                            + "% of monthly income (maximum "
                            + maximumAffordableEmi.setScale(2, RoundingMode.HALF_UP).toPlainString() + ")."
            );
        }
    }
}
