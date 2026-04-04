package com.expensetracker.expenseTracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator
        implements ConstraintValidator<ValidPassword, String> {

    private static final int    MIN_LENGTH       = 8;
    private static final int    MAX_LENGTH       = 64;
    private static final String UPPERCASE        = ".*[A-Z].*";
    private static final String LOWERCASE        = ".*[a-z].*";
    private static final String DIGIT            = ".*[0-9].*";
    private static final String SPECIAL          = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";
    private static final String NO_WHITESPACE    = "^\\S+$";

    @Override
    public boolean isValid(String password,
                           ConstraintValidatorContext context) {

        // Disable default message
        context.disableDefaultConstraintViolation();

        if (password == null || password.isBlank()) {
            buildViolation(context, "Password is required");
            return false;
        }

        // Collect all failures at once
        boolean valid = true;

        if (password.length() < MIN_LENGTH) {
            buildViolation(context,
                    "Password must be at least " + MIN_LENGTH + " characters");
            valid = false;
        }

        if (password.length() > MAX_LENGTH) {
            buildViolation(context,
                    "Password must not exceed " + MAX_LENGTH + " characters");
            valid = false;
        }

        if (!password.matches(UPPERCASE)) {
            buildViolation(context,
                    "Password must contain at least one uppercase letter (A-Z)");
            valid = false;
        }

        if (!password.matches(LOWERCASE)) {
            buildViolation(context,
                    "Password must contain at least one lowercase letter (a-z)");
            valid = false;
        }

        if (!password.matches(DIGIT)) {
            buildViolation(context,
                    "Password must contain at least one number (0-9)");
            valid = false;
        }

        if (!password.matches(SPECIAL)) {
            buildViolation(context,
                    "Password must contain at least one special character (!@#$%^&*)");
            valid = false;
        }

        if (!password.matches(NO_WHITESPACE)) {
            buildViolation(context,
                    "Password must not contain spaces");
            valid = false;
        }

        return valid;
    }

    private void buildViolation(ConstraintValidatorContext ctx,
                                String message) {
        ctx.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}