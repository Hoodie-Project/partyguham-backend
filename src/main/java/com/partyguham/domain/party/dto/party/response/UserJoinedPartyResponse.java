package com.partyguham.domain.party.dto.party.response;
import com.partyguham.domain.party.entity.PartyStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserJoinedPartyResponse {

    private long total;
    private List<PartyUserItem> partyUsers;

    @Getter
    @Builder
    public static class PartyUserItem {
        private Long id;
        private String createdAt;
        private PositionDto position;
        private PartyDto party;
    }

    @Getter
    @Builder
    public static class PositionDto {
        private String main;
        private String sub;
    }

    @Getter
    @Builder
    public static class PartyDto {
        private Long id;
        private String title;
        private String image;
        private PartyStatus partyStatus;
        private PartyTypeDto partyType;
    }

    @Getter
    @Builder
    public static class PartyTypeDto {
        private String type;
    }
}