package com.sprint.lending.dto;

import com.sprint.lending.entity.LoanStatus;

import java.math.BigDecimal;

public record AdminLoanReviewResponse(
        Long loanId,
        String borrowerName,
        String borrowerEmail,
        BigDecimal principal,
        Integer tenureMonths,
        BigDecimal emi,
        LoanStatus status,
        String reviewRemark
) {
}
