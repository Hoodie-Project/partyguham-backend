package com.partyguham.domain.recruitment.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort.Direction;

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
public class PartyRecruitmentsRequest {

    private String sort = "createdAt";

    private Direction order = Direction.ASC;       

    private String main;

    @NotNull
    private Boolean completed = false; // 프론트 변경요청

}


