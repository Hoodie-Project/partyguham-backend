package com.partyguham.user.profile.dto.response;

import com.partyguham.user.profile.entity.CareerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CareerResponse {
    private Long id;
    private Long positionId;
    private Integer years;
    private CareerType careerType;
}