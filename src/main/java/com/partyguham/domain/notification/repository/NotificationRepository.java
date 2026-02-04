package com.partyguham.domain.notification.repository;

import com.partyguham.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByUserIdAndIsCheckedFalse(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.isChecked = true " +
            "where n.user.id = :userId and n.isChecked = false")
    void markAllAsCheckedByUserId(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

    @Modifying // Select가 아닌 수정을 알림
    @Query("delete from Notification n where n.createdAt < :targetDate")
    void deleteOldNotifications(@Param("targetDate") LocalDateTime targetDate);

}