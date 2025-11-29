package com.partyguham.party.dto.partyAdmin.mapper;

import com.partyguham.party.dto.partyAdmin.response.GetAdminPartyUsersResponseDto;
import com.partyguham.party.entity.PartyUser;
import org.springframework.stereotype.Component;

@Component
public class PartyUserAdminMapper {

    /**
     * PartyUser 엔티티 → 관리자용 파티원 DTO로 변환
     */
    public GetAdminPartyUsersResponseDto.AdminPartyUserDto toAdminDto(PartyUser pu) {
        return GetAdminPartyUsersResponseDto.AdminPartyUserDto.builder()
                .id(pu.getId())
                .authority(
                        pu.getAuthority() != null
                                ? pu.getAuthority().name().toLowerCase()
                                : null
                )
                .user(
                        GetAdminPartyUsersResponseDto.UserSummary.builder()
                                .nickname(pu.getUser().getNickname())
                                .image(pu.getUser().getProfile().getImage())
                                .build()
                )
                .position(
                        pu.getPosition() != null
                                ? GetAdminPartyUsersResponseDto.PositionSummary.builder()
                                .main(pu.getPosition().getMain())
                                .sub(pu.getPosition().getSub())
                                .build()
                                : null
                )
                .createdAt(pu.getCreatedAt())
                .updatedAt(pu.getUpdatedAt())
                .status(
                        pu.getStatus() != null
                                ? pu.getStatus().name().toLowerCase()
                                : null
                )
                .build();
    }
}