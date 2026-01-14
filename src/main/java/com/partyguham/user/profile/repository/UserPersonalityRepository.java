package com.partyguham.user.profile.repository;


import com.partyguham.user.profile.entity.UserPersonality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPersonalityRepository extends JpaRepository<UserPersonality, Long> {

    /** 유저의 전체 성향 응답 조회 (Fetch Join으로 성능 최적화 권장) */
    @Query("select up from UserPersonality up join fetch up.question join fetch up.personalityOption where up.user.id = :userId")
    List<UserPersonality> findByUserId(@Param("userId") Long userId);

    /** 유저의 전체 응답 삭제 (벌크 삭제) */
    @Modifying
    @Query("delete from UserPersonality up where up.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /** 특정 질문들에 대한 기존 응답 한 번에 삭제 (Upsert 시 사용) */
    @Modifying
    @Query("delete from UserPersonality up where up.user.id = :userId and up.question.id in :questionIds")
    void deleteByUserIdAndQuestion_IdIn(@Param("userId") Long userId, @Param("questionIds") List<Long> questionIds);

    /** 특정 질문 하나에 대한 응답 전체 삭제 */
    @Modifying
    @Query("delete from UserPersonality up where up.user.id = :userId and up.question.id = :questionId")
    void deleteByUserIdAndQuestion_Id(@Param("userId") Long userId, @Param("questionId") Long questionId);

    /** 특정 옵션 하나만 선택 취소 */
    @Modifying
    @Query("delete from UserPersonality up where up.user.id = :userId and up.personalityOption.id = :optionId")
    void deleteByUserIdAndOptionId(@Param("userId") Long userId, @Param("optionId") Long optionId);

    List<UserPersonality> findByUserIdAndQuestionIdIn(Long userId, List<Long> questionIds);
}