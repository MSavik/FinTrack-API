package com.fintrack.fintrack_api.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String errorMessage) {
        super(errorMessage);
    }
}
