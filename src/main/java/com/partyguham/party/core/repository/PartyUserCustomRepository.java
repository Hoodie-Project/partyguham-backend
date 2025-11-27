package com.partyguham.party.core.repository;

import com.partyguham.party.core.entity.PartyUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartyUserCustomRepository {
    Page<PartyUser> findPartyUsers(
            Long partyId,
            String main,
            String nickname,
            String sort,
            String order,
            Pageable pageable
    );
}

