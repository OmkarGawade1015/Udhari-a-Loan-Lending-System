package com.sprint.lending.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanApplicationRequest(
        @NotNull(message = "Principal is required.")
        @DecimalMin(value = "0.01", message = "Principal must be greater than zero.")
        @Digits(integer = 10, fraction = 2, message = "Principal supports up to 10 integer and 2 decimal digits.")
        BigDecimal principal,

        @NotNull(message = "Tenure is required.")
        @Min(value = 3, message = "Tenure must be at least 3 months.")
        @Max(value = 60, message = "Tenure must be at most 60 months.")
        Integer tenureMonths
) {
}
