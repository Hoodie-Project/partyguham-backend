package com.partyguham.domain.user.profile.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserLocationBulkRequest {

    // 관심 지역 목록 (최대 3개)
    @Size(min= 1, max = 3, message = "관심 지역은 최소 1개, 최대 3개까지 설정할 수 있습니다.")
    private List<@Positive(message = "지역 ID는 양수여야 합니다.") Long> locationIds;
}