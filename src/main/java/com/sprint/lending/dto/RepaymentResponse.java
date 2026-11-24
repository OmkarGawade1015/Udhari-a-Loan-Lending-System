package com.sprint.lending.dto;

import com.sprint.lending.entity.RepaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RepaymentResponse(
        Long repaymentId,
        Integer installmentNumber,
        LocalDate dueDate,
        BigDecimal amount,
        RepaymentStatus status
) {
}
