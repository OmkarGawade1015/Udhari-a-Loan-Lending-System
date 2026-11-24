package com.sprint.lending.exception;

public class LoanNotFoundException extends LendingDomainException {

    public LoanNotFoundException(String message) {
        super(message);
    }
}
