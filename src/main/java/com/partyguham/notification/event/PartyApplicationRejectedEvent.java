package com.partyguham.notification.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyApplicationRejectedEvent {

    private final Long partyId;
    private final String partyTitle;
    private final Long hostUserId;       // 알림 받을 유저
    private final String applicantNickname;
}