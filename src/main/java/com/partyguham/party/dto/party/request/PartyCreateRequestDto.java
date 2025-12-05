package com.partyguham.party.dto.party.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PartyCreateRequestDto {
    private String title;
    private String content;
    private Long partyTypeId;
    private Long positionId;
}
