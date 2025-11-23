package com.partyguham.user.profile.dto.request;

import com.partyguham.user.profile.entity.CareerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCareerCreateRequest {
    private Long positionId;
    private Integer years;
    private CareerType careerType;   // "primary" / "secondary"
}