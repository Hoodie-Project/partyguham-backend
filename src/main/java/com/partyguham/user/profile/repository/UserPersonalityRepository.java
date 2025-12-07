package com.partyguham.user.profile.repository;


import com.partyguham.user.profile.entity.UserPersonality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPersonalityRepository extends JpaRepository<UserPersonality, Long> {

    // 특정 유저 + 특정 질문에 대한 응답들
    List<UserPersonality> findByUserIdAndQuestionId(Long userId, Long questionId);

    // 유저의 전체 성향 응답
    List<UserPersonality> findByUserId(Long userId);

    // 유저 전체 삭제
    void deleteByUserId(Long userId);

    // 유저 + 질문 기준 전체 삭제
    void deleteByUserIdAndQuestion_Id(Long userId, Long questionId);

    // 유저 + 옵션 기준 1개 삭제 (user + option 유니크 가정)
    void deleteByUser_IdAndPersonalityOption_Id(Long userId, Long optionId);

    // 🔥 여러 questionId 한 번에 삭제 (IN)
    void deleteByUserIdAndQuestion_IdIn(Long userId, List<Long> questionIds);

}