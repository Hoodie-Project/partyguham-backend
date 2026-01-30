package com.partyguham.domain.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyNewMemberJoinedEvent {
    private final Long partyId;
    private final Long joinUserId;
    private final String joinUserName;
}
