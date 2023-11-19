package edu.example.validation.validators;

import edu.example.util.FolderType;
import edu.example.validation.constraints.FolderTypeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FolderTypeValidator implements ConstraintValidator<FolderTypeConstraint, String> {

    @Override
    public boolean isValid(String type, ConstraintValidatorContext constraintValidatorContext) {

        try {
            FolderType.valueOf(type);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}