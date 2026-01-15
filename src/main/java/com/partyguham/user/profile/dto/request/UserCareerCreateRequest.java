package com.partyguham.user.profile.dto.request;

import com.partyguham.user.profile.entity.CareerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCareerCreateRequest {
    @NotNull(message = "경력 타입은 필수입니다.")
    private CareerType careerType;

    @NotNull(message = "직무 ID는 필수입니다.")
    private Long positionId;

    @PositiveOrZero(message = "경력 연차는 0 이상이어야 합니다.")
    private Integer years;
}