package com.partyguham.domain.notification.event;

import lombok.Builder;
import lombok.Getter;

/**
 * 파티 지원 완료 이벤트
 * - 방장에게 "누가 어떤 파티에 지원했다"는 알림을 보내고 싶을 때 사용
 */
@Getter
@Builder
public class PartyApplicationCreatedEvent {

    private final Long partyId;
    private final String partyTitle;
    private final Long hostUserId;
    private final String applicantNickname;
    private final String partyImage;

    private final String fcmToken;
}