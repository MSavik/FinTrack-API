package com.fintrack.fintrack_api.exception;

public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String errorMessage) {
        super(errorMessage);
    }
}
