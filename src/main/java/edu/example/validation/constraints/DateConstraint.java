package edu.example.validation.constraints;

import edu.example.validation.validators.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface DateConstraint {
    String message() default "Date should be formatted YYYY-MM-DD";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
