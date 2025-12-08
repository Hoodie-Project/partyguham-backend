package com.partyguham.notification.repository;


import com.partyguham.notification.entity.Notification;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByUser_IdAndIsCheckedFalse(Long userId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.isChecked = true where n.user.id = :userId")
    void markChecked(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}