package com.sprint.lending.dto;

import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.RepaymentStatus;

public record PaymentResponse(
        Long repaymentId,
        RepaymentStatus repaymentStatus,
        LoanStatus loanStatus
) {
}
