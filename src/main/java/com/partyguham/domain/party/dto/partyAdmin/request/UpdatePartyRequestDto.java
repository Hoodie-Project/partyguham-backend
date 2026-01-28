package com.partyguham.domain.party.dto.partyAdmin.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdatePartyRequestDto {
    private Long partyTypeId;
    private String title;
    private String content;
}
