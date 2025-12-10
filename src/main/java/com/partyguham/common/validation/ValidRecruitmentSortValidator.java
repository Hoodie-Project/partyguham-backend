package com.partyguham.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidRecruitmentSortValidator implements ConstraintValidator<ValidRecruitmentSort, String> {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id",
            "createdAt",
            "updatedAt",
            "content",
            "maxParticipants",
            "currentParticipants",
            "completed"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // null이나 빈 값은 다른 validation에서 처리
        }
        return ALLOWED_SORTS.contains(value);
    }
}

