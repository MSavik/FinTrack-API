package com.fintrack.fintrack_api.exception;

public class BudgetNotFoundException extends RuntimeException {

    public BudgetNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
