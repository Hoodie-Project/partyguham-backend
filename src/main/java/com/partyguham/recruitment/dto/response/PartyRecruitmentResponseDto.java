package com.partyguham.recruitment.dto.response;

import com.partyguham.party.dto.party.response.PartyDto;
import com.partyguham.recruitment.entity.PartyRecruitment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 단일 파티 모집글 조회 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRecruitmentResponseDto {
    private PartyDto party;
    private Position position;

    private String content;
    private int recruitingCount;
    private int recruitedCount;
    private int applicationCount;
    private boolean completed;
    private LocalDateTime createdAt;

    /**
     * PartyRecruitment 엔티티를 PartyRecruitmentResponseDto로 변환하는 정적 팩토리 메서드
     */
    public static PartyRecruitmentResponseDto from(PartyRecruitment recruitment) {
        // PartyDto 매핑 (별도 파일의 PartyDto 사용)
        PartyDto partyDto = PartyDto.from(recruitment.getParty());

        // Position 매핑
        Position positionDto = Position.builder()
                .id(recruitment.getPosition().getId())
                .main(recruitment.getPosition().getMain())
                .sub(recruitment.getPosition().getSub())
                .build();

        int applicationCount = recruitment.getPartyApplications() != null
                ? recruitment.getPartyApplications().size()
                : 0;

        return PartyRecruitmentResponseDto.builder()
                .party(partyDto)
                .position(positionDto)
                .content(recruitment.getContent())
                .recruitingCount(recruitment.getMaxParticipants())
                .recruitedCount(recruitment.getCurrentParticipants())
                .applicationCount(applicationCount)
                .completed(recruitment.getCompleted())
                .createdAt(recruitment.getCreatedAt() != null
                        ? recruitment.getCreatedAt()
                        : null)
                .build();
    }

    // 내부 클래스 Party 제거 (별도 파일의 PartyDto 사용)
    
    // Position은 그대로 유지 (PositionResponse와 다른 구조)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Position {
        private Long id;
        private String main;
        private String sub;
    }
}

