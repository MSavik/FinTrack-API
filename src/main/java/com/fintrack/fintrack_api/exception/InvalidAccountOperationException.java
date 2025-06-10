package com.fintrack.fintrack_api.exception;

public class InvalidAccountOperationException extends RuntimeException {

    public InvalidAccountOperationException(String errorMessage) {
        super(errorMessage);
    }
}
