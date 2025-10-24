package com.partyguham.party.repository;

import com.partyguham.party.model.Party;
import java.util.List;

public interface PartyCustomRepository {
    List<Party> findByTitleKeyword(String keyword);

}