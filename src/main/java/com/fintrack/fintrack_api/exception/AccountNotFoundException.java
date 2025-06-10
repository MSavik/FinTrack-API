package com.fintrack.fintrack_api.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
