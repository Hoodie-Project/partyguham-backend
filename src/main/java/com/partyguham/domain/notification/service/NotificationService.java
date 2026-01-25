package com.partyguham.domain.notification.service;

import com.partyguham.domain.notification.dto.request.GetNotificationsRequest;
import com.partyguham.domain.notification.dto.response.GetNotificationsResponse;
import com.partyguham.domain.notification.entity.Notification;
import com.partyguham.domain.notification.entity.NotificationType;
import com.partyguham.domain.notification.reader.NotificationReader;
import com.partyguham.domain.notification.repository.NotificationQueryRepository;
import com.partyguham.domain.notification.repository.NotificationRepository;
import com.partyguham.domain.notification.template.NotificationTemplate;
import com.partyguham.domain.user.account.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationReader notificationReader;

    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final EntityManager entityManager;


    /** 알림 상태 호가인 */
    public boolean hasUnchecked(Long userId) {
        return notificationRepository.existsByUserIdAndIsCheckedFalse(userId);
    }

    /** 확인 처리 */
    public void markAsChecked(Long userId) {
        notificationRepository.markAllAsCheckedByUserId(userId);
    }

    /** 읽음 처리 */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification n = notificationReader.getNotification(notificationId, userId);

        n.markAsRead();
    }

    /** 삭제 */
    public void deleteNotification(Long notificationId, Long userId) {
        notificationRepository.deleteByIdAndUserId(notificationId, userId);
    }

    @Transactional(readOnly = true)
    public GetNotificationsResponse getNotifications(Long userId,
                                                     GetNotificationsRequest req) {

        // 1) size 기본값 & 최대값 제한
        int size = (req.getSize() != null && req.getSize() > 0 && req.getSize() <= 50)
                ? req.getSize()
                : 10;

        Long cursor = req.getCursor();
        NotificationType type = req.getType();

        // 3) 쿼리 호출 (커서 기반)
        Slice<Notification> slice = notificationQueryRepository
                .findNotifications(userId, size, cursor, type);

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
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_CREATED;
        String title = t.title();
        String body = t.body(partyTitle, applicantUserNickname);
        String link = "/parties/" + partyId;

        User userRef = entityManager.getReference(User.class, hostUserId);

        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
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
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_DECLINED;
        String title = t.title();
        String body = t.body(partyTitle, applicantUserNickname);
        String link = "/party/setting/applicant/" + partyId;

        User userRef = entityManager.getReference(User.class, hostUserId);


        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
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
            Long partyId,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_NEW_MEMBER_JOINED;
        String title = t.title();
        String body = t.body();
        String link = "/party/" + partyId + "#home";

        User userRef = entityManager.getReference(User.class, partyUserId);


        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /** 파티장이 수락 알림*/
    public void partyApplicationAcceptedNotification(
            Long applicantUserId,
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_ACCEPTED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/my/apply";

        User userRef = entityManager.getReference(User.class, applicantUserId);

        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /** 파티장이 거절 알림*/
    public void partyApplicationRejectedNotification(
            Long applicantUserId,
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_APPLICATION_REJECTED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/my/apply";

        User userRef = entityManager.getReference(User.class, applicantUserId);

        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
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
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_RECRUITMENT_CLOSED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/my/apply";

        User userRef = entityManager.getReference(User.class, applicationUserId);


        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
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
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_FINISHED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/party/" + partyId + "#home";

        User userRef = entityManager.getReference(User.class, partyUserId);


        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
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
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_REOPENED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/party/" + partyId + "#home";

        User userRef = entityManager.getReference(User.class, partyUserId);


        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
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
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_INFO_UPDATED;
        String title = t.title();
        String body = t.body(partyTitle);
        String link = "/party/" + partyId + "#home";

        User userRef = entityManager.getReference(User.class, partyUserId);

        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 파티유저 나감
     */
    @Transactional
    public void partyMemberLeft(
            Long partyUserId,
            String userNickname,
            Long partyId,
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_MEMBER_LEFT;
        String title = t.title();
        String body = t.body(partyTitle, userNickname);
        String link = "/party/" + partyId + "#PartyPeopleTab";

        User userRef = entityManager.getReference(User.class, partyUserId);

        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 파티유저 포지션 변경
     */
    @Transactional
    public void partyMemberPositionChangedEvent(
            Long partyUserId,
            String userNickname,
            String position,
            Long partyId,
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_MEMBER_POSITION_CHANGED;
        String title = t.title();
        String body = t.body(partyTitle, userNickname, position);
        String link = "/party/" + partyId + "#PartyPeopleTab";

        User userRef = entityManager.getReference(User.class, partyUserId);

        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 파티유저 강퇴
     */
    @Transactional
    public void partyMemberKickedEvent(
            Long partyUserId,
            String userNickname,
            Long partyId,
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_MEMBER_KICKED;
        String title = t.title();
        String body = t.body(partyTitle, userNickname);
        String link = "/party/" + partyId + "#PartyPeopleTab";

        User userRef = entityManager.getReference(User.class, partyUserId);

        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 파티장 변경
     */
    @Transactional
    public void partyLeaderChangedEvent(
            Long partyUserId,
            String userNickname,
            Long partyId,
            String partyTitle,
            String partyImage
    ) {
        NotificationTemplate t = NotificationTemplate.PARTY_LEADER_CHANGED;
        String title = t.title();
        String body = t.body(partyTitle, userNickname);
        String link = "/party/" + partyId + "#PartyPeopleTab";

        User userRef = entityManager.getReference(User.class, partyUserId);

        Notification notification = Notification.builder()
                .user(userRef)
                .type(NotificationType.PARTY)
                .title(title)
                .message(body)
                .image(partyImage)
                .link(link)
                .build();

        notificationRepository.save(notification);
    }

}