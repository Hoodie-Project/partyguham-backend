package com.partyguham.notification.dto.request;

import com.partyguham.notification.entity.NotificationType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetNotificationsRequest {

    @Min(1)
    @Max(10)
    private Integer size;
    private Long cursor;

    /**
     *     PARTY("파티 활동"),
     *     RECRUIT("지원 소식"),
     *     SYSTEM("시스템 알림");
     */
    private NotificationType type;
}