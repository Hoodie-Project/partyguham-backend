package com.partyguham.party.repository;

import com.partyguham.party.dto.party.request.GetPartiesRequest;
import com.partyguham.party.entity.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartyCustomRepository {
    Page<Party> findByTitleKeyword(String keyword, Pageable pageable);
    Page<Party> searchParties(GetPartiesRequest request, Pageable pageable);
}