package com.partyguham.domain.notification.controller;

import com.partyguham.domain.auth.jwt.UserPrincipal;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.domain.notification.dto.request.GetNotificationsRequest;
import com.partyguham.domain.notification.dto.response.GetNotificationsResponse;
import com.partyguham.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /** 사용자 알림 체크 상태 확인 */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> hasUnchecked(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        boolean result = notificationService.hasUnchecked(user.getId());

        return ResponseEntity.ok(
                Map.of("hasUnchecked", result)
        );
    }

    /** 알림 확인 처리 (isChecked = true) */
    @PatchMapping("/check")
    public ResponseEntity<Void> markAsChecked(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        notificationService.markAsChecked(user.getId());
        return ResponseEntity.noContent().build();
    }

    /** 알림 읽음 처리 */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(notificationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /** 알림 삭제 */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long notificationId
    ) {
        notificationService.deleteNotification(notificationId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /** 사용자 알림 리스트 조회 */
    @GetMapping
    public GetNotificationsResponse getNotifications(
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute GetNotificationsRequest req
    ) {
        return notificationService.getNotifications(user.getId(), req);
    }
}
