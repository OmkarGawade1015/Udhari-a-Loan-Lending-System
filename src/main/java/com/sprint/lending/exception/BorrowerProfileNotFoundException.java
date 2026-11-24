package com.sprint.lending.exception;

public class BorrowerProfileNotFoundException extends LendingDomainException {

    public BorrowerProfileNotFoundException(String message) {
        super(message);
    }
}
