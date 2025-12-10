package com.partyguham.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyRecruitmentClosedEvent {
    private final Long applicationUserId; // 지원한 유저
    private final String partyTitle;
    private final String partyImage;

    private final String fcmToken;
}
