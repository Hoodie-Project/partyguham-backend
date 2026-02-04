package com.partyguham.domain.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyMemberPositionChangedEvent {
    private final Long partyUserId;
    private final String userNickname;
    private final String position;
    private final Long partyId;
    private final String partyTitle;
    private final String partyImage;

    private final String fcmToken;
}
