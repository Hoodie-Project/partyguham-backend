package com.partyguham.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyLeaderChangedEvent {
    private final Long partyUserId;
    private final String userNickname;
    private final Long partyId;
    private final String partyTitle;

    private final String fcmToken;
}
