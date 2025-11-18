package com.partyguham.notification.service;

import com.partyguham.notification.entity.Notification;
import com.partyguham.notification.entity.NotificationType;
import com.partyguham.notification.repository.NotificationRepository;
import com.partyguham.notification.repository.NotificationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    /**
     * 파티 지원 알림 생성
     *
     * @param hostUserId      알림을 받을 파티장 유저 ID
     * @param applicantUserId 파티에 지원한 유저 ID
     * @param partyId         파티 ID
     * @param partyTitle      파티 제목
     */
    @Transactional
    public void createPartyAppliedNotification(
            Long hostUserId,
            Long applicantUserId,
            Long partyId,
            String partyTitle
    ) {

        // 1) 알림 타입 조회 (DB에 미리 넣어둔 값: type = "PARTY_APPLIED")
        NotificationType type = notificationTypeRepository.findByType("PARTY_APPLIED")
                .orElseThrow(() -> new IllegalStateException("알림 타입(PARTY_APPLIED)이 정의되어 있지 않습니다."));

        // 2) 제목/메시지/링크 구성 (원하면 메시지 포맷은 enum이나 템플릿으로 분리해도 됨)
        String title = "새로운 파티 지원이 도착했어요";
        String message = String.format("'%s' 파티에 유저(ID: %d)가 지원했습니다.", partyTitle, applicantUserId);
        String link = "/parties/" + partyId; // 프론트 상세 페이지 URL 패턴에 맞춰 수정

        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .userId(hostUserId)     // 알림 **받는** 유저
                .notificationType(type)
                .title(title)
                .message(message)
                .image(null)            // 필요하면 썸네일 이미지 URL 등 넣기
                .link(link)
                .isRead(false)
                .isChecked(false)
                .build();

        // 4) 저장
        notificationRepository.save(notification);
    }
}