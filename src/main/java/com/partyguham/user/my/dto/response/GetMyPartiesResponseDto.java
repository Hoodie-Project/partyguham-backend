package com.partyguham.user.my.dto.response;

import com.partyguham.party.entity.PartyStatus;
import com.partyguham.party.entity.PartyUser;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 내가 속한 파티 목록 응답 DTO
 */
@Getter
@Builder
public class GetMyPartiesResponseDto {

    /** 필터+페이징 후 총 개수 */
    private long total;

    /** 내가 속한 파티 목록(PartyUser 단위) */
    private List<MyPartyUserDto> partyUsers;

    @Getter
    @Builder
    public static class MyPartyUserDto {
        private Long id;                    // partyUser.id
        private LocalDateTime createdAt;    // 파티에 합류한 시간
        private PositionSummary position;   // 내 포지션
        private PartySummary party;         // 파티 정보
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
        private PartyStatus partyStatus;  // "in_progress" / "closed"
        private PartyTypeSummary partyType;
    }

    @Getter
    @Builder
    public static class PartyTypeSummary {
        private String type; // 예: "포트폴리오"
    }

    // ====== 변환 헬퍼 ======

    public static GetMyPartiesResponseDto fromEntities(List<PartyUser> partyUsers, long total) {
        List<MyPartyUserDto> items = partyUsers.stream()
                .map(GetMyPartiesResponseDto::toMyPartyUserDto)
                .toList();

        return GetMyPartiesResponseDto.builder()
                .total(total)
                .partyUsers(items)
                .build();
    }

    private static MyPartyUserDto toMyPartyUserDto(PartyUser pu) {
        var party = pu.getParty();
        var partyType = party.getPartyType();
        var position = pu.getPosition(); // null 가능

        return MyPartyUserDto.builder()
                .id(pu.getId())
                .createdAt(pu.getCreatedAt())
                .position(position != null
                        ? PositionSummary.builder()
                        .main(position.getMain())
                        .sub(position.getSub())
                        .build()
                        : null)
                .party(PartySummary.builder()
                        .id(party.getId())
                        .title(party.getTitle())
                        .image(party.getImage())
                        .partyStatus(party.getPartyStatus())
                        .partyType(PartyTypeSummary.builder()
                                .type(partyType.getType())
                                .build())
                        .build())
                .build();
    }

}