package com.partyguham.domain.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyInfoUpdatedEvent {
    private final Long partyUserId;
    private final Long partyId;
    private final String partyTitle;
    private final String partyImage;

    private final String fcmToken;
}
