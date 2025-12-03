package com.partyguham.application.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 모집 지원자 목록 조회용 검색/페이징 파라미터
 */
@Getter
@Setter
public class PartyApplicantSearchRequestDto {

    /**
     * 페이지 (1부터 시작)
     */
    @Min(1)
    private Integer page = 1;

    /**
     * 페이지당 데이터 개수
     */
    @Min(1)
    @Max(100)
    private Integer limit = 20;

    /**
     * 정렬 기준 필드
     * - 현재는 createdAt만 허용
     */
    @Pattern(regexp = "createdAt", message = "정렬 기준은 createdAt만 허용됩니다.")
    private String sort = "createdAt";

    /**
     * 정렬 방향
     * - ASC / DESC
     */
    @Pattern(regexp = "ASC|DESC", message = "정렬 방향은 ASC 또는 DESC만 허용됩니다.")
    private String order = "DESC";

    /**
     * 지원 상태 (선택)
     * - pending / processing / approved / rejected
     */
    @Pattern(
            regexp = "pending|processing|approved|rejected",
            message = "status는 pending, processing, approved, rejected 중 하나여야 합니다."
    )
    private String status; // null 이면 전체
}