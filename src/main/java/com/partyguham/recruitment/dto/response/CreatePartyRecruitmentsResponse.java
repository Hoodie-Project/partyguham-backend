package com.partyguham.recruitment.dto.response;

import com.partyguham.recruitment.entity.PartyRecruitment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 파티 모집글 생성 응답 DTO
 * 실제 필드는 추후 채워주세요.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartyRecruitmentsResponse {

    private Long id;
    private String content;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Boolean completed;
    private LocalDateTime createdAt;

    public static CreatePartyRecruitmentsResponse from(PartyRecruitment recruitment) {
        return CreatePartyRecruitmentsResponse.builder()
                .id(recruitment.getId())
                .content(recruitment.getContent())
                .maxParticipants(recruitment.getMaxParticipants())
                .currentParticipants(recruitment.getCurrentParticipants())
                .completed(recruitment.getCompleted())
                .createdAt(recruitment.getCreatedAt())
                .build();
    }
}



