package com.sprint.lending.dto;

import com.sprint.lending.entity.LoanStatus;

import java.math.BigDecimal;

public record LoanResponse(
        Long loanId,
        BigDecimal principal,
        BigDecimal annualInterestRate,
        Integer tenureMonths,
        BigDecimal emi,
        LoanStatus status,
        String reviewRemark
) {
        
}
