package com.partyguham.notification.event;

import lombok.Builder;
import lombok.Getter;

/**
 * 파티 지원 완료 이벤트
 * - 방장에게 "누가 어떤 파티에 지원했다"는 알림을 보내고 싶을 때 사용
 */
@Getter
@Builder
public class PartyAppliedEvent {

    private final Long partyId;
    private final String partyTitle;

    private final Long hostUserId;       // 알림 받을 유저(방장)
    private final Long applicantUserId;  // 지원한 유저
    private final String applicantNickname;
}