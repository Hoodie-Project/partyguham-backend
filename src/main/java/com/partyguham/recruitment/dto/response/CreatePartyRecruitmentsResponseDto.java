package com.partyguham.recruitment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
/**
 * 파티 모집글 생성 응답 DTO
 * 실제 필드는 추후 채워주세요.
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartyRecruitmentsResponseDto {

    private Long id;
    private String content;
    private Integer recruitingCount;
    private Integer recruitedCount;
    private String status;
    private LocalDateTime createdAt;
}



