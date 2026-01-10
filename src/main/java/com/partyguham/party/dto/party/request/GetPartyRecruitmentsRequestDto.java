package com.partyguham.party.dto.party.request;

import com.partyguham.common.validation.ValidMainPosition;
import com.partyguham.common.validation.ValidRecruitmentSort;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPartyRecruitmentsRequestDto {

    @Min(1)
    private Integer page = 1;             
    
    @Min(1)
    private Integer size = 5;        
    
    @ValidRecruitmentSort
    private String sort = "createdAt";         
    
    private Direction order = Direction.ASC;       

    @ValidMainPosition
    private String main;
    private List<Long> position;      
    private List<Long> partyType;  
    private String titleSearch;      
}
