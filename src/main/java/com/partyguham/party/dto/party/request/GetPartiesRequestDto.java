package com.partyguham.party.dto.party.request;

import com.partyguham.party.entity.PartyStatus;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GetPartiesRequestDto { 
    private Integer page;
    private Integer limit;
    private String sort;
    private String order;
    private PartyStatus partyStatus; 
    private List<String> partyType;
    private String titleSearch;
}