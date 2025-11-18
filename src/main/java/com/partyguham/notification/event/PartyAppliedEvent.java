package com.partyguham.notification.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 파티 지원 완료 시 발행되는 도메인 이벤트
 * - "누가 어떤 파티에 지원했다" 정보만 담는다.
 * - 순수 POJO (JPA 엔티티 아님)
 */
@Getter
@ToString
@Builder
public class PartyAppliedEvent {

    /** 지원한 파티 ID */
    private final Long partyId;

    /** 파티 제목 (알림 메시지에 사용) */
    private final String partyTitle;

    /** 파티 호스트(방장) 유저 ID → 알림 받을 사람 */
    private final Long hostUserId;

    /** 지원한 유저 ID → 알림 내용에 사용 */
    private final Long applicantUserId;
}