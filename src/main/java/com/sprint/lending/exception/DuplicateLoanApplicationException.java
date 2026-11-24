package com.sprint.lending.exception;

public class DuplicateLoanApplicationException extends LendingDomainException {

    public DuplicateLoanApplicationException(String message) {
        super(message);
    }
}
