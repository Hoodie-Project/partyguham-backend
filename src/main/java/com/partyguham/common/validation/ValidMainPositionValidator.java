package com.partyguham.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidMainPositionValidator implements ConstraintValidator<ValidMainPosition, String> {

    // TODO: 실제 DB의 Position 테이블에 있는 main 값들로 수정 필요
    private static final Set<String> ALLOWED_MAINS = Set.of(
            "기획자",
            "디자이너",
            "프론트엔드",
            "백엔드",
            "풀스택",
            "데브옵스",
            "QA",
            "PM",
            "데이터분석가",
            "AI/ML"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // null이나 빈 값은 다른 validation에서 처리
        }
        return ALLOWED_MAINS.contains(value);
    }
}

