package com.partyguham.user.profile.dto.response;

import java.util.List;

// 질문 하나에 대한 응답 (질문 ID + 선택 옵션 ID 리스트)
public record PersonalityAnswerItem(
        Long questionId,
        List<Long> optionIds
) {}