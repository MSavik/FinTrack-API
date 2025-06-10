package com.fintrack.fintrack_api.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String errorMessage) {
        super(errorMessage);
    }
}
