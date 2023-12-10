package edu.example.validation.validators;

import edu.example.validation.constraints.DateConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.sql.Date;

public class DateValidator implements ConstraintValidator<DateConstraint, String> {

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {

        try {
            Date.valueOf(date);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}