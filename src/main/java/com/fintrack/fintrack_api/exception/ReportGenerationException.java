package com.fintrack.fintrack_api.exception;

public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(String errorMessage) {
        super(errorMessage);
    }
}
