package com.sprint.lending.exception;

public class InvalidLoanStateException extends LendingDomainException {

    public InvalidLoanStateException(String message) {
        super(message);
    }
}
