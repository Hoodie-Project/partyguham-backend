package com.partyguham.party.core.dto.partyAdmin.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdatePartyRequestDto {
    private MultipartFile image;
    private Long partyTypeId;
    private String title;
    private String content;
}
