package com.partyguham.notification.entity;

import com.partyguham.common.entity.BaseEntity;
import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
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
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;    // 실제 읽음

    @Builder.Default
    @Column(nullable = false)
    private Boolean isChecked = false; // 리스트 노출 여부
}
//
//    - 알림 도착 (읽지 않은 상태, 뱃지 표시됨)
//      → isChecked = false / isRead = false
//
//        - 알림 리스트 열람 (읽지는 않았지만 뱃지는 사라짐)
//      → isChecked = true / isRead = false
//
//        - 알림 클릭하여 상세 확인 (읽음 처리 완료)
//      → isChecked = true / isRead = true