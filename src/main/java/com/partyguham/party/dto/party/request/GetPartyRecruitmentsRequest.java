package com.partyguham.party.dto.party.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPartyRecruitmentsRequest {

    @Min(1)
    private Integer page = 1;             
    
    @Min(1)
    private Integer size = 5;        
    
    private String sort = "createdAt";         
    
    private Direction order = Direction.ASC;       

    private String main;
    private List<Long> position;      
    private List<Long> partyType;  
    private String titleSearch;      
}
