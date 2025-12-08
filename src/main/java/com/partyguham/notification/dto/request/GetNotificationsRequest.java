package com.partyguham.notification.dto.request;

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
    private Integer limit;
    private Long cursor;

    /**
     * party : 파티활동
     * recruit : 지원소식
     */
    @Pattern(regexp = "party|recruit",
            message = "type은 party 또는 recruit만 허용됩니다.")
    private String type;
}