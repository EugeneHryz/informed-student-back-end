package edu.example.validation.validators;

import edu.example.validation.constraints.PasswordComplexityConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordComplexityValidator implements ConstraintValidator<PasswordComplexityConstraint, String> {

    private static final int MIN_LENGTH = 8;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        if (!s.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Password must include at least one uppercase, " +
                            "one lowercase symbol and one number")
                    .addConstraintViolation();
            return false;
        }

        if (s.length() < MIN_LENGTH) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Password is too short")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}