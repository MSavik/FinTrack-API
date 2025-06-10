package com.fintrack.fintrack_api.exception;

public class BudgetConflictException extends RuntimeException {

    public BudgetConflictException(String errorMessage) {
        super(errorMessage);
    }
}
