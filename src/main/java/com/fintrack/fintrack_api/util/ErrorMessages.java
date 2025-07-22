package com.fintrack.fintrack_api.util;

public class ErrorMessages {

    public static final String NULL_USER = "User cannot be null";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String DUPLICATE_EMAIL = "Email already exists";
    public static final String DUPLICATE_PHONE_NUMBER = "Phone number already exists";
    public static final String INVALID_EMAIL_FORMAT = "Email should be valid";
    public static final String FORBIDDEN_PASSWORD_LENGTH = "Password must be between 8 and 128 characters";
    public static final String FORBIDDEN_USER_FIRST_NAME_LENGTH = "First name must be between 2 and 50 characters";
    public static final String FORBIDDEN_USER_LAST_NAME_LENGTH = "Last name must be between 2 and 50 characters";
    public static final String INVALID_PHONE_NUMBER = "Phone number should be valid";
    public static final String INVALID_REQUEST_BODY = "Request body should be valid";
    public static final String NULL_ID = "ID should not be null";

    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}
