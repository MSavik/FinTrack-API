package com.fintrack.fintrack_api.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String errorMessage) {
        super(errorMessage);
    }
}
