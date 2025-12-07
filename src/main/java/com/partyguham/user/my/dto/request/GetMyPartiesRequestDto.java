package com.partyguham.user.my.dto.request;

import com.partyguham.party.entity.PartyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

/**
 * 내가 속한 파티 목록 조회 요청 DTO
 * - page / limit / order / partyStatus 필터
 */
@Getter
@Setter
public class GetMyPartiesRequestDto {

    /** 페이지 (1부터 시작) */
    @Min(1)
    private Integer page = 1;

    /** 페이지당 개수 */
    @Min(1)
    private Integer size = 20;

    /**
     * 정렬 방향
     * - ASC : 오래된 파티부터 (createdAt 오름차순)
     * - DESC: 최신 파티부터 (createdAt 내림차순)
     */
    private Sort.Direction order = Sort.Direction.DESC;

    /**
     * 파티 상태 필터 (optional)
     * - "in_progress" / "closed"
     * - null 이면 전체
     */
    private PartyStatus partyStatus; // IN_PROGRESS / CLOSED (대문자만 허용)

}