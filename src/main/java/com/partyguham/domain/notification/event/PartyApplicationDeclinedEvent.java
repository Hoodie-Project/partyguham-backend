package com.partyguham.domain.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyApplicationDeclinedEvent {
    private final Long partyId;
    private final String partyTitle;
    private final Long hostUserId;
    private final String applicantNickname;
    private final String partyImage;

    private final String fcmToken;
}
