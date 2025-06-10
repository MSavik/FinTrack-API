package com.fintrack.fintrack_api.exception;

public class PersistenceException  extends RuntimeException {

    public PersistenceException(String errorMessage) {
        super(errorMessage);
    }
}
