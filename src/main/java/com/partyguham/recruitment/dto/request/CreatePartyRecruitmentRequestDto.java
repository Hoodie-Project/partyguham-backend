package com.partyguham.recruitment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 파티 모집글 생성 요청 DTO
 * 실제 필드는 추후 채워주세요.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePartyRecruitmentRequestDto {
    @NotNull(message = "positionId 는 필수값입니다.")
    private Long positionId;

    @NotBlank(message = "content 는 필수값입니다.")
    private String content;

    @Min(value = 1, message = "recruiting_count 는 최소 1명 이상이어야 합니다.")
    @JsonProperty("recruiting_count")
    private int recruitingCount;
}


