package com.partyguham.notification.service;

import com.partyguham.notification.dto.request.GetNotificationsRequest;
import com.partyguham.notification.dto.response.GetNotificationsResponse;
import com.partyguham.notification.entity.Notification;
import com.partyguham.notification.entity.NotificationType;
import com.partyguham.notification.repository.NotificationQueryRepository;
import com.partyguham.notification.repository.NotificationRepository;
import com.partyguham.notification.repository.NotificationTypeRepository;
import com.partyguham.notification.template.NotificationTemplate;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;


    public boolean hasUnchecked(Long userId) {
        return notificationRepository.existsByUser_IdAndIsCheckedFalse(userId);
    }

    public void markAsChecked(Long userId) {
        notificationRepository.markChecked(userId);
    }

    /** 읽음 처리 */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification n = notificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));

        if (!Boolean.TRUE.equals(n.getIsRead())) {
            n.setIsRead(true);
        }
    }

    /** 삭제 */
    public void deleteNotification(Long notificationId, Long userId) {
        notificationRepository.deleteByIdAndUserId(notificationId, userId);
    }

    @Transactional(readOnly = true)
    public GetNotificationsResponse getNotifications(Long userId,
                                                     GetNotificationsRequest req) {

        // 1) limit 기본값 & 최대값 제한
        int limit = (req.getLimit() != null && req.getLimit() > 0 && req.getLimit() <= 50)
                ? req.getLimit()
                : 10;

        Long cursor = req.getCursor();
        String type = req.getType();

        // 2) type → notificationTypeId
        Long notificationTypeId = null;
        if (type != null && !type.isBlank()) {
            NotificationType nt = notificationTypeRepository.findByType(type)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 타입입니다. type=" + type));
            notificationTypeId = nt.getId();
        }

        // 3) 쿼리 호출 (커서 기반)
        Slice<Notification> slice = notificationQueryRepository
                .findNotifications(userId, limit, cursor, notificationTypeId);

        // 4) 응답 DTO로 변환 (빌더/정적 메서드로 위임)
        return GetNotificationsResponse.from(slice);
    }

    /**
     * 파티 지원 알림 생성
     */
    @Transactional
    public void createPartyAppliedNotification(
            Long hostUserId,
            String applicantUserNickname,
            Long partyId,
            String partyTitle
    ) {
        // 1) 알림 타입 조회
        NotificationType type = notificationTypeRepository.findByType("PARTY")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_CREATED;
        String title = t.title();
        String body = t.body(partyTitle, applicantUserNickname);
        String link = "/parties/" + partyId;

        User userRef = entityManager.getReference(User.class, hostUserId);

        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(userRef)
                .notificationType(type)
                .title(title)
                .message(body)
                .image(null)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 파티 지원자 거절 알림 생성
     */
    @Transactional
    public void createPartyDeclinedNotification(
            Long hostUserId,
            String applicantUserNickname,
            Long partyId,
            String partyTitle
    ) {
        // 1) 알림 타입 조회
        NotificationType type = notificationTypeRepository.findByType("PARTY")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_DECLINED;
        String title = t.title();
        String body = t.body(partyTitle, applicantUserNickname);
        String link = "/party/setting/applicant/" + partyId;

        User userRef = entityManager.getReference(User.class, hostUserId);


        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(userRef)
                .notificationType(type)
                .title(title)
                .message(body)
                .image(null)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 파티 지원자 수락, 최종 합류
     */
    @Transactional
    public void PartyNewMemberNotification(
            Long partyUserId,
            Long partyId
    ) {
        // 1) 알림 타입 조회
        NotificationType type = notificationTypeRepository.findByType("PARTY")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        NotificationTemplate t = NotificationTemplate.PARTY_NEW_MEMBER_JOINED;
        String title = t.title();
        String body = t.body();
        String link = "/party/" + partyId + "#home";

        User userRef = entityManager.getReference(User.class, partyUserId);


        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(userRef)
                .notificationType(type)
                .title(title)
                .message(body)
                .image(null)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /** 파티장이 수락 알림*/
    public void partyApplicationAcceptedNotification(
            Long applicantUserId,
            String partyTitle
    ) {
        // 1) 알림 타입 조회
        NotificationType type = notificationTypeRepository.findByType("PARTY")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_ACCEPTED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/my/apply";

        User userRef = entityManager.getReference(User.class, applicantUserId);

        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(userRef)
                .notificationType(type)
                .title(title)
                .message(body)
                .image(null)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /** 파티장이 거절 알림*/
    public void partyApplicationRejectedNotification(
            Long applicantUserId,
            String partyTitle
    ) {
        // 1) 알림 타입 조회
        NotificationType type = notificationTypeRepository.findByType("PARTY")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_REJECTED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/my/apply";

        User userRef = entityManager.getReference(User.class, applicantUserId);

        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(userRef)
                .notificationType(type)
                .title(title)
                .message(body)
                .image(null)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 모집 완료
     */
    @Transactional
    public void PartyRecruitmentClosed(
            Long applicationUserId,
            String partyTitle
    ) {
        // 1) 알림 타입 조회
        NotificationType type = notificationTypeRepository.findByType("RECRUIT")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        NotificationTemplate t = NotificationTemplate.PARTY_RECRUITMENT_CLOSED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/my/apply";

        User userRef = entityManager.getReference(User.class, applicationUserId);


        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(userRef)
                .notificationType(type)
                .title(title)
                .message(body)
                .image(null)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 파티 종료
     */
    @Transactional
    public void partyFinished(
            Long partyUserId,
            Long partyId,
            String partyTitle
    ) {
        NotificationType type = notificationTypeRepository.findByType("PARTY")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        NotificationTemplate t = NotificationTemplate.PARTY_FINISHED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/party/" + partyId + "#home";

        User userRef = entityManager.getReference(User.class, partyUserId);


        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(userRef)
                .notificationType(type)
                .title(title)
                .message(body)
                .image(null)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 파티 재활성화
     */
    @Transactional
    public void partyReopened(
            Long partyUserId,
            Long partyId,
            String partyTitle
    ) {
        NotificationType type = notificationTypeRepository.findByType("PARTY")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        NotificationTemplate t = NotificationTemplate.PARTY_REOPENED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/party/" + partyId + "#home";

        User userRef = entityManager.getReference(User.class, partyUserId);


        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(userRef)
                .notificationType(type)
                .title(title)
                .message(body)
                .image(null)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 파티 업데이트
     */
    @Transactional
    public void partyInfoUpdated(
            Long partyUserId,
            Long partyId,
            String partyTitle
    ) {
        NotificationType type = notificationTypeRepository.findByType("PARTY")
                .orElseThrow(() -> new IllegalStateException(
                        "알림 타입(PARTY)이 정의되어 있지 않습니다."
                ));

        // 2) 제목/메시지/링크 구성
        NotificationTemplate t = NotificationTemplate.PARTY_INFO_UPDATED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/party/" + partyId + "#home";

        User userRef = entityManager.getReference(User.class, partyUserId);

        // 3) Notification 엔티티 생성
        Notification notification = Notification.builder()
                .user(userRef)
                .notificationType(type)
                .title(title)
                .message(body)
                .image(null)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }


}