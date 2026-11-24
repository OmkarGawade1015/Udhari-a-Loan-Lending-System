package com.sprint.lending.dto;

import com.sprint.lending.entity.UserRole;

import java.math.BigDecimal;

public record RegistrationResponse(
        Long userId,
        String email,
        UserRole role,
        String fullName,
        BigDecimal monthlyIncome,
        String employmentType
) {
}
