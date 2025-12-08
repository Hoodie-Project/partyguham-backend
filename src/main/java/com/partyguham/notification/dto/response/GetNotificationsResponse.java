package com.partyguham.notification.dto.response;

import com.partyguham.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetNotificationsResponse {

    private List<NotificationItem> notifications;
    private Long nextCursor;

    @Getter
    @Builder
    public static class NotificationItem {
        private Long id;
        private NotificationTypeDto notificationType;
        private String title;
        private String message;
        private String image;
        private String link;
        private Boolean isRead;
        private Boolean isChecked;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class NotificationTypeDto {
        private String type;
        private String label;
    }

    // 🔥 서비스에서 바로 호출할 팩토리 메서드
    public static GetNotificationsResponse from(Slice<Notification> slice) {
        List<NotificationItem> items = slice.getContent().stream()
                .map(n -> NotificationItem.builder()
                        .id(n.getId())
                        .notificationType(
                                NotificationTypeDto.builder()
                                        .type(n.getNotificationType().getType())
                                        .label(n.getNotificationType().getLabel())
                                        .build()
                        )
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .image(n.getImage())
                        .link(n.getLink())
                        .isRead(n.getIsRead())
                        .isChecked(n.getIsChecked())
                        .createdAt(n.getCreatedAt())
                        .build()
                )
                .toList();

        Long nextCursor = items.isEmpty()
                ? null
                : items.get(items.size() - 1).getId();

        return GetNotificationsResponse.builder()
                .notifications(items)
                .nextCursor(nextCursor)
                .build();
    }
}