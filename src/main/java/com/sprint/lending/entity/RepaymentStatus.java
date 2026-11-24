package com.sprint.lending.entity;

public enum RepaymentStatus {
    PENDING,
    PAID;

    public boolean canTransitionTo(RepaymentStatus next) {
        return this == PENDING && next == PAID;
    }
}
