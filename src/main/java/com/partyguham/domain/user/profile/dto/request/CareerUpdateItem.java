package com.partyguham.domain.user.profile.dto.request;

public record CareerUpdateItem(
        Long id,    // 어떤 걸 수정할지 식별자 필수
        Long positionId,
        Integer years
) {}