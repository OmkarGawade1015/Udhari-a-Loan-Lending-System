package com.sprint.lending.entity;

public enum LoanStatus {
    APPLIED,
    APPROVED,
    REJECTED,
    CLOSED;

    public boolean canTransitionTo(LoanStatus next) {
        if (next == null) {
            return false;
        }

        return switch (this) {
            case APPLIED -> next == APPROVED || next == REJECTED;
            case APPROVED -> next == CLOSED;
            case REJECTED, CLOSED -> false;
        };
    }
}
