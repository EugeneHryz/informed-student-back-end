package edu.example.validation.constraints;

import edu.example.validation.validators.PasswordComplexityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordComplexityValidator.class)
public @interface PasswordComplexityConstraint {
    String message() default "Password not secure enough";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
