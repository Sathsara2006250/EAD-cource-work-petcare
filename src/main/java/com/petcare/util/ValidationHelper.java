package com.petcare.util;

import com.petcare.exception.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Validation helper utility enforcing business rules and input format validity.
 */
public class ValidationHelper {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{7,15}$"
    );

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Validates required non-blank string fields.
     */
    public static void requireNonEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
    }

    /**
     * Validates email address format.
     */
    public static void validateEmail(String email) throws ValidationException {
        requireNonEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: '" + email + "'. Example: user@domain.com");
        }
    }

    /**
     * Validates phone number format.
     */
    public static void validatePhone(String phone) throws ValidationException {
        requireNonEmpty(phone, "Phone number");
        String cleanPhone = phone.replaceAll("[\\s-()]", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new ValidationException("Invalid phone number format: '" + phone + "'. Must contain 7 to 15 digits.");
        }
    }

    /**
     * Validates positive integer (e.g. Pet Age).
     */
    public static int validatePositiveInt(String value, String fieldName) throws ValidationException {
        requireNonEmpty(value, fieldName);
        try {
            int num = Integer.parseInt(value.trim());
            if (num < 0) {
                throw new ValidationException(fieldName + " cannot be negative.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid integer.");
        }
    }

    /**
     * Validates positive double (e.g. Weight, Treatment Cost).
     */
    public static double validatePositiveDouble(String value, String fieldName) throws ValidationException {
        requireNonEmpty(value, fieldName);
        try {
            double num = Double.parseDouble(value.trim());
            if (num < 0.0) {
                throw new ValidationException(fieldName + " cannot be negative.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid number.");
        }
    }

    /**
     * Validates standard date string format (yyyy-MM-dd).
     */
    public static LocalDate validateDate(String dateStr, String fieldName) throws ValidationException {
        requireNonEmpty(dateStr, fieldName);
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " must follow format YYYY-MM-DD (e.g., 2025-03-25).");
        }
    }
}
