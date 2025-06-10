package com.fintrack.fintrack_api.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String errorMessage) {
        super(errorMessage);
    }
}
