package com.partyguham.domain.notification.event;

import com.partyguham.domain.application.entity.PartyApplication;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PartyRecruitmentClosedEvent {
    private final Long partyId;
    private final Long recruitmentId;
    private final List<Long> pendingUserIds;
}
