package com.partyguham.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyApplicationAcceptedEvent {
    private final Long applicantUserId;
    private final Long partyId;
    private final String partyTitle;

    private final String fcmToken;
}
