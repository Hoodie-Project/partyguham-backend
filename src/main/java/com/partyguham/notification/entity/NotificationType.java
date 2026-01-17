package com.partyguham.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    PARTY("파티 활동"),
    RECRUIT("지원 소식"),
    SYSTEM("시스템 알림");

    private final String label;
}