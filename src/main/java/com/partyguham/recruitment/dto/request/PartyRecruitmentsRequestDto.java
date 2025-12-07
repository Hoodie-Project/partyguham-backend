package com.partyguham.recruitment.dto.request;


import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;


/**
 * 파티 모집 목록 조회 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRecruitmentsRequestDto {

    @Builder.Default
    private String sort = "createdAt";

    @Builder.Default
    private String order = "ASC";

    @Nullable
    private String main;

    private Boolean completed; // 프론트 변경요청

}


