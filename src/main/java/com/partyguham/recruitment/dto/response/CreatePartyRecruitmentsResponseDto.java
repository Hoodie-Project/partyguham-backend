package com.partyguham.recruitment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 파티 모집글 생성 응답 DTO
 * 실제 필드는 추후 채워주세요.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePartyRecruitmentsResponseDto {

    private Long id;              
    private String content;      
    private int recruitingCount; 
    private int recruitedCount;   
    private String status;        
    private String createdAt;    
}



