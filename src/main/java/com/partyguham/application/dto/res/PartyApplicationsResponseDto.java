package com.partyguham.application.dto.res;

import com.partyguham.application.entity.PartyApplication;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 모집 지원자 목록 응답 DTO
 */
@Getter
@Builder
public class PartyApplicationsResponseDto {

    /**
     * 필터 + 페이징 후 전체 개수
     */
    private long total;

    /**
     * 지원자 목록
     */
    private List<PartyApplicationUserDto> applications;

    @Getter
    @Builder
    public static class PartyApplicationUserDto {
        private Long id;
        private String message;
        private String applicationStatus;      // pending / approved ...
        private LocalDateTime createdAt;
        private UserSummary user;
    }

    @Getter
    @Builder
    public static class UserSummary {
        private Long id;
        private String nickname;
        private String image; // 프로필 이미지 URL or key
    }

    public static PartyApplicationsResponseDto fromEntities(List<PartyApplication> apps,
                                                            long total) {
        List<PartyApplicationUserDto> items = apps.stream()
                .map(PartyApplicationsResponseDto::toUserDto)
                .toList();

        return PartyApplicationsResponseDto.builder()
                .total(total)
                .applications(items)
                .build();
    }

    private static PartyApplicationUserDto toUserDto(PartyApplication app) {
        var user = app.getUser();
        var profile = user.getProfile();

        return PartyApplicationUserDto.builder()
                .id(app.getId())
                .message(app.getMessage())
                .applicationStatus(app.getApplicationStatus().name().toLowerCase())
                .createdAt(app.getCreatedAt())
                .user(UserSummary.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .image(profile != null ? profile.getImage() : null)
                        .build())
                .build();
    }
}