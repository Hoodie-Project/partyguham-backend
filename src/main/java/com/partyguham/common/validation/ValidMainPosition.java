package com.partyguham.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidMainPositionValidator.class)
public @interface ValidMainPosition {
    String message() default "허용되지 않은 직군입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

