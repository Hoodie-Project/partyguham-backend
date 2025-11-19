package com.partyguham.user.profile.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserCareerBulkCreateRequest {
    private List<UserCareerCreateRequest> careers;
}