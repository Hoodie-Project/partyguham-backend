//package com.partyguham.recruitment.dto.response;
//
//import com.partyguham.recruitment.entity.PartyRecruitment;
//import com.partyguham.catalog.dto.response.PositionResponse;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class PartyRecruitmentDto {
//
//    private Long id;
//    private String content;
//    private int recruitingCount;
//    private int recruitedCount;
//    private Boolean completed;
//    private String createdAt;
//
//    private PartyDto party;
//    private PositionResponse position;
//
//    /**
//     * PartyRecruitment 엔티티를 PartyRecruitmentDto로 변환하는 정적 팩토리 메서드
//     */
//    public static PartyRecruitmentDto from(PartyRecruitment recruitment) {
//        // PartyDto 생성
//        PartyDto.PartyTypeDto partyTypeDto = PartyDto.PartyTypeDto.builder()
//                .type(recruitment.getParty().getPartyType().getType())
//                .build();
//
//        PartyDto partyDto = PartyDto.builder()
//                .id(recruitment.getParty().getId())
//                .title(recruitment.getParty().getTitle())
//                .image(recruitment.getParty().getImage())
//                .partyType(partyTypeDto)
//                .build();
//
//        // PositionResponse 매핑
//        PositionResponse positionResponse = PositionResponse.from(recruitment.getPosition());
//
//        return PartyRecruitmentDto.builder()
//                .id(recruitment.getId())
//                .content(recruitment.getContent())
//                .recruitingCount(recruitment.getMaxParticipants())
//                .recruitedCount(recruitment.getCurrentParticipants())
//                .completed(recruitment.getCompleted())
//                .createdAt(recruitment.getCreatedAt() != null
//                        ? recruitment.getCreatedAt().toString()
//                        : null)
//                .party(partyDto)
//                .position(positionResponse)
//                .build();
//    }
//
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class PartyDto {
//        private Long id;
//        private String title;
//        private String image;
//        private PartyTypeDto partyType;
//
//        @Getter
//        @Setter
//        @NoArgsConstructor
//        @AllArgsConstructor
//        @Builder
//        public static class PartyTypeDto {
//            private String type;
//        }
//    }
//}
//
