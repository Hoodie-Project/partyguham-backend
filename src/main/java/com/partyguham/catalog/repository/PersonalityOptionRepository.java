package com.partyguham.catalog.repository;

import com.partyguham.catalog.entity.PersonalityOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalityOptionRepository extends JpaRepository<PersonalityOption, Long> {

    // ✅ 질문 ID로 옵션 리스트 조회
    List<PersonalityOption> findByQuestionId(Long questionId);
}