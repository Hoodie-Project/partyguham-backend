package com.partyguham.domain.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyNewMemberJoinedEvent {
    private final Long partyUserId;
    private final Long partyId;
    private final String partyImage;
    private final String joinUserName;
    private final String PartyTitle;


    private final String fcmToken;
}
