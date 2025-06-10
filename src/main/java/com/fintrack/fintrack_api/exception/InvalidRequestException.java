package com.fintrack.fintrack_api.exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String errorMessage) {
        super(errorMessage);
    }
}
