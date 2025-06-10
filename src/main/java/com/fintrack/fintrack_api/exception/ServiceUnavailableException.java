package com.fintrack.fintrack_api.exception;

public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String errorMessage) {
        super(errorMessage);
    }
}
