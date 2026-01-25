package com.partyguham.domain.party.dto.partyAdmin.response;

import com.partyguham.domain.party.entity.PartyUser;
import com.partyguham.domain.user.account.entity.User;
import com.partyguham.domain.user.profile.entity.UserProfile;
import com.partyguham.global.entity.Status;
import org.springframework.data.domain.Page;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetAdminPartyUsersResponseDto {

    /** 파티 전체 인원 수 (DELETED 제외, 필터 상관 없음) */
    private long totalPartyUserCount;

    /** 필터 + 페이징 적용 후 전체 개수 */
    private long total;

    /** 파티원 목록 */
    private List<AdminPartyUserDto> partyUsers;

    @Getter
    @Builder
    public static class AdminPartyUserDto {
        private Long id;
        private String authority; // "master"
        private UserSummary user;
        private PositionSummary position;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String status; // "active"
    }

    @Getter
    @Builder
    public static class UserSummary {
        private String nickname;
        private String image;
    }

    @Getter
    @Builder
    public static class PositionSummary {
        private String main;
        private String sub;
    }

    // ====== 팩토리 ======

    public static GetAdminPartyUsersResponseDto from(long totalPartyUserCount,
                                                     Page<PartyUser> page) {

        List<AdminPartyUserDto> list = page.getContent().stream()
                .map(GetAdminPartyUsersResponseDto::toAdminPartyUserDto)
                .toList();

        return GetAdminPartyUsersResponseDto.builder()
                .totalPartyUserCount(totalPartyUserCount)
                .total(page.getTotalElements())
                .partyUsers(list)
                .build();
    }

    private static AdminPartyUserDto toAdminPartyUserDto(PartyUser pu) {
        String authority = pu.getAuthority() != null
                ? pu.getAuthority().name().toLowerCase()
                : null;

        User userEntity = pu.getUser();
        UserProfile profile = (userEntity != null ? userEntity.getProfile() : null);

        UserSummary userSummary = null;
        if (userEntity != null) {
            userSummary = UserSummary.builder()
                    .nickname(userEntity.getNickname())
                    .image(profile != null ? profile.getImage() : null)
                    .build();
        }

        PositionSummary positionSummary = null;
        if (pu.getPosition() != null) {
            positionSummary = PositionSummary.builder()
                    .main(pu.getPosition().getMain())
                    .sub(pu.getPosition().getSub())
                    .build();
        }

        Status status = pu.getStatus();
        String statusString = (status != null) ? status.toJson() : null;

        return AdminPartyUserDto.builder()
                .id(pu.getId())
                .authority(authority)
                .user(userSummary)
                .position(positionSummary)
                .createdAt(pu.getCreatedAt())
                .updatedAt(pu.getUpdatedAt())
                .status(statusString) // "active"
                .build();
    }
}