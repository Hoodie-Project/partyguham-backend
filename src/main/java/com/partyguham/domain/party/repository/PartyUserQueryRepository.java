package com.partyguham.domain.party.repository;

import com.partyguham.domain.party.dto.partyAdmin.request.GetAdminPartyUsersRequestDto;
import com.partyguham.domain.party.entity.PartyAuthority;
import com.partyguham.domain.party.entity.PartyUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartyUserQueryRepository {

    /**
     * 삭제되지 않은 파티원의 전체 수
     * - 필터와 상관없이 "이 파티에 몇 명 있냐?"를 위해 사용
     */
    long countAllByPartyIdNotDeleted(Long partyId);

    /**
     * 관리자용 파티원 목록 조회 (필터 + 페이징)
     */
    Page<PartyUser> searchAdminPartyUsers(Long partyId,
                                          GetAdminPartyUsersRequestDto req,
                                          Pageable pageable);

    Page<PartyUser> searchAdminPartyUsers(Long partyId,
                                          String main,              // position.main
                                          PartyAuthority authority,
                                          String nickname,
                                          String sort,
                                          String order,
                                          Pageable pageable);

    List<PartyUser> findByUserNickname(String nickname);
}