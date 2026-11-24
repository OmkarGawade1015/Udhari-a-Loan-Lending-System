package com.sprint.lending.dto;

import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @Size(max = 500, message = "Remark must be at most 500 characters.")
        String remark
) {
}
