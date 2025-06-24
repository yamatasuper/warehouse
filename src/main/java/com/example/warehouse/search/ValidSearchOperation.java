package com.example.warehouse.search;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SearchOperationValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSearchOperation {
    String message() default "Invalid search operation";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
