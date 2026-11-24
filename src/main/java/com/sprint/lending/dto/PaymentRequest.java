package com.sprint.lending.dto;

import jakarta.validation.constraints.AssertTrue;

public record PaymentRequest(
        @AssertTrue(message = "Confirm that you want to mark this instalment paid.")
        Boolean confirm
) {
}
