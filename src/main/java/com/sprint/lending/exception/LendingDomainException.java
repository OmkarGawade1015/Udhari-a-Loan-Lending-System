package com.sprint.lending.exception;

public abstract class LendingDomainException extends RuntimeException {

    protected LendingDomainException(String message) {
        super(message);
    }

    protected LendingDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
