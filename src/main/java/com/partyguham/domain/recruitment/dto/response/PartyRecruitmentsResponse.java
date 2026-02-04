package com.partyguham.domain.recruitment.dto.response;

/**
 * 특정 파티의 모집글 목록 조회 응답 DTO
 * 실제 필드는 추후 채워주세요.
 */

import com.partyguham.domain.recruitment.entity.PartyRecruitment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 파티 모집 목록 조회 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRecruitmentsResponse {
    private Long id;
    private String content;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer applicationCount;
    private Boolean completed;
    private LocalDateTime createdAt;
    private PositionDto position;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionDto {
        private String main;
        private String sub;
    }

    /**
     * PartyRecruitment 엔티티를 PartyRecruitmentsResponse로 변환하는 정적 팩토리 메서드
     */
    public static PartyRecruitmentsResponse from(PartyRecruitment recruitment) {
        PositionDto positionDto = PositionDto.builder()
                .main(recruitment.getPosition().getMain())
                .sub(recruitment.getPosition().getSub())
                .build();

        return PartyRecruitmentsResponse.builder()
                .id(recruitment.getId())
                .content(recruitment.getContent())
                .maxParticipants(recruitment.getMaxParticipants())
                .currentParticipants(recruitment.getCurrentParticipants())
                .applicationCount(recruitment.getPartyApplications() != null ? 
                        recruitment.getPartyApplications().size() : 0)
                .completed(recruitment.getCompleted())
                .createdAt(recruitment.getCreatedAt())
                .position(positionDto)
                .build();
    }
}