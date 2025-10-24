package com.partyguham.party.repository;

import com.partyguham.party.model.Party;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PartyRepositoryImpl implements PartyCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Party> findByTitleKeyword(String keyword) {
        return em.createQuery(
                        "SELECT p FROM Party p WHERE p.title LIKE :keyword", Party.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }
}