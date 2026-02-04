package com.partyguham.domain.catalog.repository;

import com.partyguham.domain.catalog.entity.PersonalityOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalityOptionRepository extends JpaRepository<PersonalityOption, Long> {

    List<PersonalityOption> findByIdIn(List<Long> ids);
}