package com.partyguham.user.profile.dto.response;

import com.partyguham.catalog.entity.Location;
import com.partyguham.user.profile.entity.UserLocation;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLocationResponse {
    private Long id;
    private Long locationId;
    private String province;
    private String city;

    // 엔티티 → DTO 변환
    public static UserLocationResponse from(UserLocation ul) {
        Location loc = ul.getLocation();

        return UserLocationResponse.builder()
                .id(ul.getId())
                .locationId(loc.getId())
                .province(loc.getProvince())
                .city(loc.getCity())
                .build();
    }
}