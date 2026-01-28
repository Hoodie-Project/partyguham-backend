package com.partyguham.domain.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyApplicationRejectedEvent {

    private final Long applicantUserId;
    private final Long partyId;
    private final String partyTitle;
    private final String partyImage;

    private final String fcmToken;
}