package com.partyguham.party.core.dto.party.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyUserDto {

    private Long id;
    private String authority; // master / deputy / member

    private PositionDto position;

    private UserDto user;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionDto {
        private Long id;
        private String main;
        private String sub;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private String nickname;
        private String image;
        private List<CareerDto> userCareers;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CareerDto {
        private Long positionId;
        private int years;
    }

    public static PartyUserDto from(com.partyguham.party.core.entity.PartyUser partyUser) {
        return PartyUserDto.builder()
                .id(partyUser.getId())
                .authority(partyUser.getAuthority().name())
                .position(
                        partyUser.getPosition() != null ?
                                PositionDto.builder()
                                        .id(partyUser.getPosition().getId())
                                        .main(partyUser.getPosition().getMain())
                                        .sub(partyUser.getPosition().getSub())
                                        .build()
                                : null
                )
                .user(
                        UserDto.builder()
                                .id(partyUser.getUser().getId())
                                .nickname(partyUser.getUser().getNickname())
                                .image(
                                        partyUser.getUser().getProfile() != null ?
                                                partyUser.getUser().getProfile().getImage() : null
                                )
                                .userCareers(List.of()) // UserCareer는 별도로 설정
                                .build()
                )
                .build();
    }

    public static PartyUserDto from(com.partyguham.party.core.entity.PartyUser partyUser, List<com.partyguham.user.profile.entity.UserCareer> userCareers) {
        return PartyUserDto.builder()
                .id(partyUser.getId())
                .authority(partyUser.getAuthority().name())
                .position(
                        partyUser.getPosition() != null ?
                                PositionDto.builder()
                                        .id(partyUser.getPosition().getId())
                                        .main(partyUser.getPosition().getMain())
                                        .sub(partyUser.getPosition().getSub())
                                        .build()
                                : null
                )
                .user(
                        UserDto.builder()
                                .id(partyUser.getUser().getId())
                                .nickname(partyUser.getUser().getNickname())
                                .image(
                                        partyUser.getUser().getProfile() != null ?
                                                partyUser.getUser().getProfile().getImage() : null
                                )
                                .userCareers(
                                        userCareers != null ?
                                                userCareers.stream()
                                                        .map(career -> CareerDto.builder()
                                                                .positionId(career.getPosition().getId())
                                                                .years(career.getYears())
                                                                .build())
                                                        .toList()
                                                : List.of()
                                )
                                .build()
                )
                .build();
    }
}
