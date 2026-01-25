package com.partyguham.domain.user.profile.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCareerRequest {
    @Min(0)
    private Integer years;

    @Min(0)
    private Long positionId;

    public boolean isEmpty() {
        return positionId == null && years == null;
    }
}
