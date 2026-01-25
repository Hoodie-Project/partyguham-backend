package com.partyguham.domain.recruitment.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort.Direction;

/**
 * 개인화된 파티 모집 목록 조회 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetPartyRecruitmentsPersonalizedRequest {

    @Min(1)
    private int page = 1;
    
    @Min(1)
    private int size = 20;

    private String sort = "createdAt";

    private Direction order = Direction.ASC;
}





