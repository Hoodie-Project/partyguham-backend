package com.partyguham.user.profile.repository;


import com.partyguham.user.profile.entity.UserPersonality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPersonalityRepository extends JpaRepository<UserPersonality, Long> {

    // 특정 유저 + 특정 질문에 대한 응답들
    List<UserPersonality> findByUserIdAndQuestionId(Long userId, Long questionId);

    // 유저의 전체 성향 응답
    List<UserPersonality> findByUserId(Long userId);

    // 질문 단위로 삭제
    void deleteByUserIdAndQuestionId(Long userId, Long questionId);
}