package com.partyguham.notification.service;

import com.partyguham.notification.entity.Notification;
import com.partyguham.notification.entity.NotificationType;
import com.partyguham.notification.repository.NotificationRepository;
import com.partyguham.notification.repository.NotificationTypeRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final UserRepository userRepository;


    public boolean hasUnchecked(Long userId) {
        return notificationRepository.existsByUser_IdAndIsCheckedFalse(userId);
    }

    public void markAsChecked(Long userId) {
        notificationRepository.markChecked(userId);
    }

    /**
     * 파티 지원 알림 생성
     */
    @Transactional
    public void createPartyAppliedNotification(
            Long hostUserId,
            Long applicantUserId,
            Long partyId,
            String partyTitle
    ) {
        // 0) 알림 받을 유저(파티장) 조회
        User hostUser = userRepository.findById(hostUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 유저입니다. id=" + hostUserId
                ));

        // 1) 알림 타입 조회 (DB에 미리: type = "PARTY_APPLIED")
        NotificationType type = notificationTypeRepository.findByType("PARTY_APPLIED")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY_APPLIED)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        String title = "새로운 파티 지원이 도착했어요";
        String message = String.format("'%s' 파티에 유저(ID: %d)가 지원했습니다.",
                partyTitle, applicantUserId);
        String link = "/parties/" + partyId;

        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(hostUser)          // ← 여기!
                .notificationType(type)
                .title(title)
                .message(message)
                .image(null)
                .link(link)
                .build();

        // 4) 저장
        notificationRepository.save(notification);
    }
}