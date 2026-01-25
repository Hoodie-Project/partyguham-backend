package com.partyguham.domain.notification.reader;

import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.notification.entity.Notification;
import com.partyguham.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.partyguham.domain.notification.exception.NotificationErrorCode.*;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationReader {
    private final NotificationRepository notificationRepository;

    public Optional<Notification> readNotification(Long id) {
        return notificationRepository.findById(id);
    }

    public Notification getNotification(Long id) {
        return this.readNotification(id)
                .orElseThrow(() -> new BusinessException(NOTIFICATION_NOT_FOUND));
    }

    public Optional<Notification> readNotification(Long id, Long userId) {
        return notificationRepository.findByIdAndUserId(id, userId);
    }

    public Notification getNotification(Long id, Long userId) {
        return notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(NOTIFICATION_NOT_FOUND));
    }



}
