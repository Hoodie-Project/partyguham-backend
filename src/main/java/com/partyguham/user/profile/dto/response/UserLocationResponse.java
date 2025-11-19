package com.partyguham.user.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserLocationResponse {

    private Long id;          // user_location PK
    private Long locationId;  // location PK
    private String province;
    private String city;
}