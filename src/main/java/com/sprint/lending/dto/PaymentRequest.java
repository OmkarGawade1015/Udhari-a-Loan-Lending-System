package com.sprint.lending.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull(message = "Payment confirmation is required.")
        @AssertTrue(message = "Confirm that you want to mark this instalment paid.")
        Boolean confirm
) {
}
