package com.partyguham.party.dto.party.request;

import com.partyguham.party.entity.PartyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import org.springframework.data.domain.Sort.Direction;

/**
 * 파티 목록 조회 요청 DTO
 */
@Getter
@Setter
public class GetPartiesRequestDto { 
    @NotNull
    @Min(1)
    private Integer page = 1;

    @NotNull
    @Min(1)
    private Integer size = 5;

    @NotNull
    private String sort = "createdAt";

    @NotNull
    private Direction order = Direction.ASC;       

    private PartyStatus partyStatus = PartyStatus.IN_PROGRESS; 
    private List<@Positive Long> partyType;
    private String titleSearch;
}