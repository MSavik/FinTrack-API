package com.fintrack.fintrack_api.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
