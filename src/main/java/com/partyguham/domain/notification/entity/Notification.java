package com.partyguham.domain.notification.entity;

import com.partyguham.global.entity.BaseEntity;
import com.partyguham.domain.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user", columnList = "user_id"),
                @Index(name = "idx_notifications_user_read", columnList = "user_id,is_read")
        }
)
@SequenceGenerator(
        name = "notifications_seq_gen",
        sequenceName = "notifications_id_seq",   // DB에 생성된 시퀀스 이름
        allocationSize = 1
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifications_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING) // Enum 명칭을 문자열로 저장
    @Column(nullable = false, length = 50)
    private NotificationType type;

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


    /**
     * 비즈니스 로직: 알림 리스트 확인 (isChecked 업데이트)
     */
    public void markAsChecked() {
        this.isChecked = true;
    }

    /**
     * 비즈니스 로직: 알림 상세 클릭 (isRead 업데이트)
     */
    public void markAsRead() {
        this.isChecked = true; // 읽었으면 당연히 체크된 것
        this.isRead = true;
    }
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