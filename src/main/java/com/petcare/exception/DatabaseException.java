package com.petcare.exception;

/**
 * Custom Exception for database connection and SQL query execution errors.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
