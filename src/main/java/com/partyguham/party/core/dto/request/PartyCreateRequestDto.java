package com.partyguham.party.core.dto.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PartyCreateRequestDto {
    private MultipartFile image;
    private String title;
    private String content;
    private Long partyTypeId;
    private Long positionId;
}
