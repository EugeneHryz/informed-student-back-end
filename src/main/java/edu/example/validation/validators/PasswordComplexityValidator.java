package edu.example.validation.validators;

import edu.example.validation.constraints.PasswordComplexityConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordComplexityValidator implements ConstraintValidator<PasswordComplexityConstraint, String> {

    private static final int minLength = 8;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        if (!s.matches("[a-zA-Z0-9]+")) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Only latin letters and numbers are allowed")
                    .addConstraintViolation();
            return false;
        }

        if (s.length() < minLength) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Password too short")
                    .addConstraintViolation();
            return false;
        }

        if (s.chars().noneMatch(Character::isUpperCase)
            || s.chars().noneMatch(Character::isLowerCase)
            || s.chars().noneMatch(Character::isDigit)) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(
                            "Password must include at least one uppercase and one lowercase symbol and one number")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
