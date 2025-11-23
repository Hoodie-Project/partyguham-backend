package com.partyguham.catalog.repository;

import com.partyguham.catalog.entity.PersonalityOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalityOptionRepository extends JpaRepository<PersonalityOption, Long> {

    List<PersonalityOption> findByIdIn(List<Long> ids);
}