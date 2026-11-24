package com.sprint.lending.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegistrationRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Enter a valid email address.")
        @Size(max = 254, message = "Email must be at most 254 characters.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        String password,

        @NotBlank(message = "Full name is required.")
        @Size(max = 120, message = "Full name must be at most 120 characters.")
        String fullName,

        @NotNull(message = "Monthly income is required.")
        @DecimalMin(value = "0.01", message = "Monthly income must be greater than zero.")
        @Digits(integer = 10, fraction = 2, message = "Monthly income supports up to 10 integer and 2 decimal digits.")
        BigDecimal monthlyIncome,

        @NotBlank(message = "Employment type is required.")
        @Size(max = 40, message = "Employment type must be at most 40 characters.")
        String employmentType
) {
}
