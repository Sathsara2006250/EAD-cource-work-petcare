package com.petcare.exception;

/**
 * Custom Exception thrown when form validation fails (e.g. invalid email format,
 * invalid phone number, blank required fields, negative values).
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
