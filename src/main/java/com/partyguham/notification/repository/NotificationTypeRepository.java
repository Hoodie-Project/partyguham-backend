package com.partyguham.notification.repository;

import com.partyguham.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    Optional<NotificationType> findByType(String type);
}