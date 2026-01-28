package com.partyguham.domain.user.profile.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserCareerBulkCreateRequest {
    @NotEmpty(message = "경력 정보는 최소 한 개 이상이어야 합니다.")
    @Size(min = 1, max = 2, message = "경력은 1 또는 2개(PRIMARY, SECONDARY)까지 등록 가능합니다.")
    @Valid // 리스트 내부의 객체들도 검증
    private List<UserCareerCreateRequest> careers;
}