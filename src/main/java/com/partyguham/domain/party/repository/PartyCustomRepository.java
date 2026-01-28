package com.partyguham.domain.party.repository;

import com.partyguham.domain.party.dto.party.request.GetPartiesRequest;
import com.partyguham.domain.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartyCustomRepository {
    List<Party> findByTitleKeyword(String keyword);
    Page<Party> findByTitleKeyword(String keyword, Pageable pageable);
    Page<Party> searchParties(GetPartiesRequest request, Pageable pageable);
}