package com.partyguham.domain.catalog.repository;

import com.partyguham.domain.catalog.entity.PersonalityQuestion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalityQuestionRepository extends JpaRepository<PersonalityQuestion, Long> {

    @EntityGraph(attributePaths = "personalityOptions")
    List<PersonalityQuestion> findAll();
}