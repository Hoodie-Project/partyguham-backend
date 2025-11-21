package com.partyguham.party.core.repository;

import com.partyguham.party.core.entity.PartyUser;
import com.partyguham.user.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyUserRepository extends JpaRepository<User, Long> {
    void save(PartyUser masterUser);
}
