package com.sprint.lending.exception;

public class AuthorizationException extends LendingDomainException {

    public AuthorizationException(String message) {
        super(message);
    }
}
