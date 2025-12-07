package com.partyguham.user.my.dto.request;

import com.partyguham.application.entity.PartyApplicationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class GetMyPartyApplicationsRequestDto {
    @Min(1)
    private Integer page;   // 1부터 시작

    @Min(1)
    private Integer size;   // 페이지 크기

    /**
     * 정렬 방향
     * - ASC / DESC (대문자만 허용)
     */
    private Sort.Direction order = Sort.Direction.DESC;

    /**
     * 지원 상태 필터 (선택)
     * - PENDING / PROCESSING / APPROVED / REJECTED
     */
    private PartyApplicationStatus partyApplicationStatus;
}