package com.fintrack.fintrack_api.exception.advisor;

import com.fintrack.fintrack_api.exception.*;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ProblemDetail> handleUnexpectedErrors(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, e.getMessage());
        problemDetail.setTitle("Internal server error");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    /* Violation and System Errors */

    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<ProblemDetail> handleEntityValidationException(ValidationException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Entity validation failed.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid Parameters.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, e.getMessage());
        problemDetail.setTitle("Database constraint violations.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    private ResponseEntity<ProblemDetail> handleServiceUnavailableException(ServiceUnavailableException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(SERVICE_UNAVAILABLE, e.getMessage());
        problemDetail.setTitle("External service failures.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(InvalidRequestException.class)
    private ResponseEntity<ProblemDetail> handleInvalidRequestException(InvalidRequestException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Generic request validation failure.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Method argument not valid.");
        problemDetail.setType(URI.create(""));

        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        problemDetail.setProperty("errors", errors);

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(PersistenceException.class)
    private ResponseEntity<ProblemDetail> handlePersistenceException(PersistenceException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, e.getMessage());
        problemDetail.setTitle("Failed to save entity due to an unexpected error.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(BusinessException.class)
    ProblemDetail handleBusinessException(BusinessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        problemDetail.setType(URI.create(""));
        return problemDetail;
    }

    /* Authentication and User Management */

    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<ProblemDetail> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, e.getMessage());
        problemDetail.setTitle("Email/username conflict during registration.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    private ResponseEntity<ProblemDetail> handleInvalidCredentialsException(InvalidCredentialsException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(UNAUTHORIZED, e.getMessage());
        problemDetail.setTitle("Failed login attempt.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(InvalidTokenException.class)
    private ResponseEntity<ProblemDetail> handleInvalidTokenException(InvalidTokenException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(FORBIDDEN, e.getMessage());
        problemDetail.setTitle("Expired/malformed JWT or password reset token.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    private ResponseEntity<ProblemDetail> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(FORBIDDEN, e.getMessage());
        problemDetail.setTitle("Role-based access violation.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    /* Account Management */

    @ExceptionHandler(AccountNotFoundException.class)
    private ResponseEntity<ProblemDetail> handleAccountNotFoundException(AccountNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Invalid account identifier in requests.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(InvalidAccountOperationException.class)
    private ResponseEntity<ProblemDetail> handleInvalidAccountOperationException(InvalidAccountOperationException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid account operation.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    /* Transaction Management */

    @ExceptionHandler(TransactionNotFoundException.class)
    private ResponseEntity<ProblemDetail> handleTransactionNotFoundException(TransactionNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Invalid transaction ID.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    private ResponseEntity<ProblemDetail> handleInvalidTransactionException(InvalidTransactionException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Business rule violations.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    private ResponseEntity<ProblemDetail> handleInsufficientFundsException(InsufficientFundsException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Attempt to spend beyond account balance.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    /* Budget Management */

    @ExceptionHandler(BudgetNotFoundException.class)
    private ResponseEntity<ProblemDetail> handleBudgetNotFoundException(BudgetNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Invalid budget ID.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(BudgetConflictException.class)
    private ResponseEntity<ProblemDetail> handleBudgetConflictException(BudgetConflictException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(CONFLICT, e.getMessage());
        problemDetail.setTitle("Duplicate budget for the same category/month.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    /* Reporting */

    @ExceptionHandler(ReportGenerationException.class)
    private ResponseEntity<ProblemDetail> handleReportGenerationException(ReportGenerationException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Failed to generate analytics.");
        problemDetail.setType(URI.create(""));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }
}
