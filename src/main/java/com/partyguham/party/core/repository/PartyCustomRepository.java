package com.partyguham.party.core.repository;

import com.partyguham.party.core.entity.Party;
import java.util.List;

public interface PartyCustomRepository {
    List<Party> findByTitleKeyword(String keyword);

}