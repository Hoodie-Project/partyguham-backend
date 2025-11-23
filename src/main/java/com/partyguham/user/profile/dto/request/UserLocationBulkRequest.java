package com.partyguham.user.profile.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserLocationBulkRequest {

    // 관심 지역 목록 (최대 3개)
    private List<Long> locationIds;
}