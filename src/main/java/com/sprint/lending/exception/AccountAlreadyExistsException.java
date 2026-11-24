package com.sprint.lending.exception;

public class AccountAlreadyExistsException extends LendingDomainException {

    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
