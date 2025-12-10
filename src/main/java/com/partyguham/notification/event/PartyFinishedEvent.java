package com.partyguham.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyFinishedEvent {
    private final Long partyUserId;
    private final Long partyId;
    private final String partyTitle;

    private final String fcmToken;
}
