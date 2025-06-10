package com.fintrack.fintrack_api.exception;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String errorMessage) {
        super(errorMessage);
    }
}
