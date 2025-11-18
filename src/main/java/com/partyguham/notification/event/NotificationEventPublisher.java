package com.partyguham.notification.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 도메인 서비스/애그리게잇에서 알림 이벤트를 발행할 때 사용하는 헬퍼
 *
 * 사용 예:
 *  - PartyApplicationService 안에서
 *    notificationEventPublisher.publishPartyApplied(...);
 */
@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 파티 지원 완료 이벤트 발행
     *
     * @param partyId        파티 ID
     * @param partyTitle     파티 제목
     * @param hostUserId     방장 유저 ID (알림 받을 대상)
     * @param applicantUserId 지원한 유저 ID
     */
    public void publishPartyApplied(Long partyId,
                                    String partyTitle,
                                    Long hostUserId,
                                    Long applicantUserId) {

        PartyAppliedEvent event = PartyAppliedEvent.builder()
                .partyId(partyId)
                .partyTitle(partyTitle)
                .hostUserId(hostUserId)
                .applicantUserId(applicantUserId)
                .build();

        eventPublisher.publishEvent(event);
    }
}

