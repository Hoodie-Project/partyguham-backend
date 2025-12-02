package com.partyguham.recruitment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 단일 파티 모집글 조회 응답 DTO
 * 실제 필드는 추후 채워주세요.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyRecruitmentResponseDto {
    private Party party;          
    private Position position;    

    private String content;          
    private int recruitingCount;    
    private int recruitedCount;       
    private int applicationCount;        
    private String status;             
    private String createdAt;         


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Party {
        private Long id;
        private String title;
        private String image;
        private String status;
        private PartyType partyType;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PartyType {
            private String type;
        }
    }

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

