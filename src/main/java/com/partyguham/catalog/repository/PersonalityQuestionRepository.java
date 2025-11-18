package com.partyguham.catalog.repository;

import com.partyguham.catalog.entity.PersonalityQuestion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalityQuestionRepository extends JpaRepository<PersonalityQuestion, Long> {

    // ✅ 질문 + 옵션들을 한 번에 조회 (N+1 방지용)
    @EntityGraph(attributePaths = "options")
    List<PersonalityQuestion> findAll();
}