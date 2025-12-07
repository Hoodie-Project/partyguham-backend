package com.partyguham.user.my.dto.response;

import com.partyguham.application.entity.PartyApplication;
import com.partyguham.application.entity.PartyApplicationStatus;
import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyStatus;
import com.partyguham.recruitment.entity.PartyRecruitment;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetMyPartyApplicationsResponseDto {

    private long total;
    private List<MyPartyApplicationDto> partyApplications;

    @Getter
    @Builder
    public static class MyPartyApplicationDto {
        private Long id;
        private String message;
        private PartyApplicationStatus partyApplicationStatus;      // PENDING / APPROVED ...
        private LocalDateTime createdAt;
        private PartyRecruitmentSummary partyRecruitment;
    }

    @Getter
    @Builder
    public static class PartyRecruitmentSummary {
        private Long id;
        private Boolean completed;
        private PositionSummary position;
        private PartySummary party;
    }

    @Getter
    @Builder
    public static class PositionSummary {
        private String main;
        private String sub;
    }

    @Getter
    @Builder
    public static class PartySummary {
        private Long id;
        private String title;
        private String image;
        private PartyStatus partyStatus;              // "CLOSED" / "IN_PROGRESS" 등
        private String partyType;  // partyType.type
    }

    public static GetMyPartyApplicationsResponseDto from(Page<PartyApplication> page) {
        List<MyPartyApplicationDto> items = page.getContent().stream()
                .map(GetMyPartyApplicationsResponseDto::toDto)
                .toList();

        return GetMyPartyApplicationsResponseDto.builder()
                .total(page.getTotalElements())
                .partyApplications(items)
                .build();
    }

    private static MyPartyApplicationDto toDto(PartyApplication app) {
        PartyRecruitment pr = app.getPartyRecruitment();
        Party party = pr.getParty();

        return MyPartyApplicationDto.builder()
                .id(app.getId())
                .message(app.getMessage())
                .partyApplicationStatus(app.getApplicationStatus()) // PENDING / APPROVED 그대로
                .createdAt(app.getCreatedAt())
                .partyRecruitment(
                        PartyRecruitmentSummary.builder()
                                .id(pr.getId())
                                .completed(pr.getCompleted())
                                .position(
                                        PositionSummary.builder()
                                                .main(pr.getPosition().getMain())
                                                .sub(pr.getPosition().getSub())
                                                .build()
                                )
                                .party(
                                        PartySummary.builder()
                                                .id(party.getId())
                                                .title(party.getTitle())
                                                .image(party.getImage())
                                                .partyType(party.getPartyType().getType())
                                                .partyStatus(party.getPartyStatus())
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}