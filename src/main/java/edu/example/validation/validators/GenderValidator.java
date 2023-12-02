package edu.example.validation.validators;

import edu.example.model.Gender;
import edu.example.validation.constraints.GenderConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<GenderConstraint, String> {

    @Override
    public boolean isValid(String gender, ConstraintValidatorContext constraintValidatorContext) {

        try {
            Gender.valueOf(gender);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}