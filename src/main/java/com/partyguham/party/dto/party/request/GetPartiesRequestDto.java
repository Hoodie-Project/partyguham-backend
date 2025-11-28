package com.partyguham.party.dto.party.request;


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
    private String status;
    private List<String> partyType;
    private String titleSearch;
}