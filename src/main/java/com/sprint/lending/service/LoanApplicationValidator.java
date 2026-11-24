package com.sprint.lending.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoanApplicationValidator {

    private static final int MINIMUM_TENURE_MONTHS = 3;
    private static final int MAXIMUM_TENURE_MONTHS = 60;

    private final BigDecimal minimumPrincipal;
    private final BigDecimal maximumPrincipal;

    public LoanApplicationValidator(
            @Value("${app.loan.minimum-principal}") BigDecimal minimumPrincipal,
            @Value("${app.loan.maximum-principal}") BigDecimal maximumPrincipal
    ) {
        this.minimumPrincipal = minimumPrincipal;
        this.maximumPrincipal = maximumPrincipal;
    }

    public void validate(BigDecimal principal, Integer tenureMonths) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal is required.");
        }
        if (principal.signum() <= 0) {
            throw new IllegalArgumentException("Principal must be greater than zero.");
        }
        if (principal.compareTo(minimumPrincipal) < 0 || principal.compareTo(maximumPrincipal) > 0) {
            throw new IllegalArgumentException(
                    "Principal must be between " + minimumPrincipal.toPlainString()
                            + " and " + maximumPrincipal.toPlainString() + "."
            );
        }
        if (tenureMonths == null) {
            throw new IllegalArgumentException("Tenure is required.");
        }
        if (tenureMonths < MINIMUM_TENURE_MONTHS || tenureMonths > MAXIMUM_TENURE_MONTHS) {
            throw new IllegalArgumentException(
                    "Tenure must be between " + MINIMUM_TENURE_MONTHS
                            + " and " + MAXIMUM_TENURE_MONTHS + " months."
            );
        }
    }

    public BigDecimal getMinimumPrincipal() {
        return minimumPrincipal;
    }

    public BigDecimal getMaximumPrincipal() {
        return maximumPrincipal;
    }
}
