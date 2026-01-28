package com.partyguham.domain.party.dto.partyAdmin.request;

import com.partyguham.domain.party.entity.PartyAuthority;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 파티원 목록 조회 요청 DTO (관리자용)
 */
@Getter
@Setter
public class GetAdminPartyUsersRequestDto {

    // 0-base 페이지
    @Min(0)
    private Integer page = 0;

    // 페이지 사이즈 (기본 20)
    @Min(1)
    @Max(100)
    private Integer size = 20;

    /**
     * 정렬 기준 컬럼
     * - id, createdAt, updatedAt 정도만 허용 (원하면 더 추가)
     */
    @Pattern(
            regexp = "id|createdAt|updatedAt",
            message = "sort는 id, createdAt, updatedAt만 허용됩니다."
    )
    private String sort = "createdAt";

    /**
     * 정렬 방향
     * - asc / desc
     */
    @Pattern(
            regexp = "asc|desc",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "order는 asc 또는 desc만 허용됩니다."
    )
    private String order = "desc";

    /**
     * 직군(main) 필터
     * - 기획자 / 디자이너 / 개발자 / 마케터/광고
     */
    @Pattern(
            regexp = "기획자|디자이너|개발자|마케터/광고",
            message = "main은 기획자, 디자이너, 개발자, 마케터/광고만 허용됩니다."
    )
    private String main;   // 선택값이니까 @NotBlank 안 씀

    // 권한 필터 (MASTER/DEPUTY/MEMBER)
    private PartyAuthority authority;

    // 닉네임 검색
    private String nickname;
}