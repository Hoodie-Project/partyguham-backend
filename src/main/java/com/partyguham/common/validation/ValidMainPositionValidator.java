package com.partyguham.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidMainPositionValidator implements ConstraintValidator<ValidMainPosition, String> {

    private static final Set<String> ALLOWED_MAINS = Set.of(
            "기획자",
            "디자이너",
            "개발자",
            "마케터/광고"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; 
        }
        return ALLOWED_MAINS.contains(value);
    }
}

