package com.petcare.exception;

/**
 * Custom Exception thrown when an appointment booking or scheduling violates
 * business rules (e.g. past dates, double-booking, missing pet/customer link).
 */
public class InvalidAppointmentException extends Exception {
    
    public InvalidAppointmentException(String message) {
        super(message);
    }

    public InvalidAppointmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
