package com.partyguham.domain.recruitment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**

파티 모집 공고 상태 변경/삭제 등

다수의 ID 입력을 위한 Request DTO
*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRecruitmentIdsBodyRequest {
    
    @NotEmpty(message = "최소 1개 이상의 모집 공고 ID가 포함되어야 합니다.")
    private List<Long> partyRecruitmentIds;
}