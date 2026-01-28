package com.partyguham.domain.recruitment.dto.response;

import com.partyguham.domain.recruitment.entity.PartyRecruitment;
import com.partyguham.domain.catalog.dto.response.PositionResponse;
import com.partyguham.domain.party.dto.party.PartyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class PartyRecruitmentResponse {

   private Long id;
   private String content;
   private Integer maxParticipants;
   private Integer currentParticipants;
   private Boolean completed;
   private String createdAt;

   private PartyDto party;
   private PositionResponse position;

   /**
    * PartyRecruitment 엔티티를 PartyRecruitmentDto로 변환하는 정적 팩토리 메서드
    */
   public static PartyRecruitmentResponse from(PartyRecruitment recruitment) {
       // PartyDto 매핑 (별도 파일의 PartyDto 사용)
       PartyDto partyDto = PartyDto.from(recruitment.getParty());

       // PositionResponse 매핑
       PositionResponse positionResponse = PositionResponse.from(recruitment.getPosition());

       return PartyRecruitmentResponse.builder()
               .id(recruitment.getId())
               .content(recruitment.getContent())
               .maxParticipants(recruitment.getMaxParticipants())
               .currentParticipants(recruitment.getCurrentParticipants())
               .completed(recruitment.getCompleted())
               .createdAt(recruitment.getCreatedAt() != null
                       ? recruitment.getCreatedAt().toString()
                       : null)
               .party(partyDto)
               .position(positionResponse)
               .build();
   }
}

