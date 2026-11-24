package com.sprint.lending.dto;

import com.sprint.lending.entity.UserRole;

public record AuthResponse(
        String token,
        Long userId,
        String email,
        UserRole role
) {
}
