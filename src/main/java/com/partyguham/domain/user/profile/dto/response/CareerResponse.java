package com.partyguham.domain.user.profile.dto.response;

import com.partyguham.domain.catalog.entity.Position;
import com.partyguham.domain.user.profile.entity.CareerType;
import com.partyguham.domain.user.profile.entity.UserCareer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerResponse {

    private Long id;              // user_career id
    private Integer years;        // 경력 연차
    private CareerType careerType;

    // Position 정보를 통째로 들고가는 중첩 DTO
    private PositionResponse position;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionResponse {
        private Long id;
        private String main;
        private String sub;
    }

    /**
     * UserCareer 엔티티 -> CareerResponse 변환 헬퍼
     * - 서비스에서 map(CareerResponse::from) 으로 공통 사용
     */
    public static CareerResponse from(UserCareer career) {
        Position p = career.getPosition();

        return CareerResponse.builder()
                .id(career.getId())
                .years(career.getYears())
                .careerType(career.getCareerType())
                .position(
                        PositionResponse.builder()
                                .id(p.getId())
                                .main(p.getMain())
                                .sub(p.getSub())
                                .build()
                )
                .build();
    }
}