package com.partyguham.party.repository;

import com.partyguham.party.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartyCustomRepository {
    List<Party> findByTitleKeyword(String keyword);
    Page<Party> findByTitleKeyword(String keyword, Pageable pageable);
    Page<Party> searchParties(GetPartiesRequestDto request, Pageable pageable);
}