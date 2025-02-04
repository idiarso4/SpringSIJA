package com.school.security.validator;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 6;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (password != null) {
            if (!UPPERCASE_PATTERN.matcher(password).find()) {
                errors.add("Password must contain at least one uppercase letter");
            }

            if (!LOWERCASE_PATTERN.matcher(password).find()) {
                errors.add("Password must contain at least one lowercase letter");
            }

            if (!DIGIT_PATTERN.matcher(password).find()) {
                errors.add("Password must contain at least one number");
            }

            if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
                errors.add("Password must contain at least one special character");
            }

            // Check for common patterns
            if (containsSequentialNumbers(password)) {
                errors.add("Password should not contain sequential numbers");
            }

            if (containsRepeatedCharacters(password)) {
                errors.add("Password should not contain repeated characters");
            }
        }

        return errors;
    }

    private boolean containsSequentialNumbers(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (Character.isDigit(password.charAt(i)) &&
                Character.isDigit(password.charAt(i + 1)) &&
                Character.isDigit(password.charAt(i + 2))) {
                int num1 = Character.getNumericValue(password.charAt(i));
                int num2 = Character.getNumericValue(password.charAt(i + 1));
                int num3 = Character.getNumericValue(password.charAt(i + 2));
                if (num2 == num1 + 1 && num3 == num2 + 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsRepeatedCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) &&
                password.charAt(i) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }
}
