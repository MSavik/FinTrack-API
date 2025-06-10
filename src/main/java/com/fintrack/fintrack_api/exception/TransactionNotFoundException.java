package com.fintrack.fintrack_api.exception;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
