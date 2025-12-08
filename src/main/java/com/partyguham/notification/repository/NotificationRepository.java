package com.partyguham.notification.repository;


import com.partyguham.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByUser_IdAndIsCheckedFalse(Long userId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Notification n set n.isChecked = true where n.user.id = :userId")
    void markChecked(Long userId);

    Optional<Notification> findByIdAndUser_Id(Long id, Long userId);

    /** 본인꺼만 삭제 */
    void deleteByIdAndUserId(Long id, Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}