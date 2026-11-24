package com.sprint.lending.exception;

public class RepaymentNotFoundException extends LendingDomainException {

    public RepaymentNotFoundException(String message) {
        super(message);
    }
}
