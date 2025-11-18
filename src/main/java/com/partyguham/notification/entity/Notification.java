package com.partyguham.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notification",
        indexes = {
                @Index(name = "idx_notification_user", columnList = "user_id"),
                @Index(name = "idx_notification_user_read", columnList = "user_id,is_read")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 지금 Nest도 userId 숫자만 들고 있으니까 그대로
    @Column(name = "user_id", nullable = false)
    private Long userId;
    // 필요하면 ManyToOne으로 바꿔도 됨:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", nullable = false)
    // private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_type_id", nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(columnDefinition = "text")
    private String image;

    @Column(length = 255)
    private String link;

    @Column(nullable = false)
    private boolean isRead = false;    // 실제 읽음

    @Column(nullable = false)
    private boolean isChecked = false; // 리스트 노출 여부

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}