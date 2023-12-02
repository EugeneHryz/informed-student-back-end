package edu.example.validation.constraints;

import edu.example.validation.validators.GenderValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
public @interface GenderConstraint {
    String message() default "The specified gender does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
