package com.partyguham.notification.controller;

import com.partyguham.auth.jwt.UserPrincipal;
import com.partyguham.common.annotation.ApiV2Controller;
import com.partyguham.notification.dto.request.GetNotificationsRequest;
import com.partyguham.notification.dto.response.GetNotificationsResponse;
import com.partyguham.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 사용자 알림 체크 상태 확인 */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> hasUnchecked(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        boolean result = notificationService.hasUnchecked(user.getId());

        return ResponseEntity.ok(
                Map.of("hasUnchecked", result)
        );
    }

    /**
     * 알림 확인 처리 (isChecked = true) */
    @PatchMapping("/check")
    public ResponseEntity<Void> markAsChecked(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        notificationService.markAsChecked(user.getId());
        return ResponseEntity.noContent().build();
    }


    /**
     * 사용자 알림 리스트 조회
     */
    @GetMapping
    public GetNotificationsResponse getNotifications(
            @AuthenticationPrincipal UserPrincipal user,
            @ModelAttribute GetNotificationsRequest req
    ) {
        return notificationService.getNotifications(user.getId(), req);
    }



    // 알림 읽음처리

    // 삭제처리
}
