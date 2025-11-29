package com.partyguham.party.dto.partyAdmin.request;

import com.partyguham.party.entity.PartyAuthority;
import lombok.Getter;
import lombok.Setter;

/**
 * 파티원 목록 조회 요청 DTO (관리자용)
 */
@Getter
@Setter
public class GetAdminPartyUsersRequestDto {

    // 페이지 번호 (0-based or 1-based는 컨벤션에 맞게 조정)
    private Integer page = 0;

    // 페이지 크기
    private Integer size = 20;

    // 권한 필터 (MASTER / DEPUTY / MEMBER) - optional
    private PartyAuthority authority;

    // 닉네임 검색어
    private String nickname;

    // position.main 필터
    private String main;
}