package com.partyguham.notification.repository;


import com.partyguham.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByUserIdAndIsCheckedFalse(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.isChecked = true " +
            "where n.user.id = :userId and n.isChecked = false")
    void markAllAsCheckedByUserId(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}