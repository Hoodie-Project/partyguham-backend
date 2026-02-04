package com.partyguham.domain.user.profile.repository;

import com.partyguham.domain.user.profile.entity.CareerType;
import com.partyguham.domain.user.profile.entity.UserCareer;
import com.partyguham.domain.user.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCareerRepository extends JpaRepository<UserCareer, Long> {

    List<UserCareer> findByUser(User user);

    List<UserCareer> findAllByIdInAndUserId(List<Long> ids, Long userId);

    /**
     * 유저 ID로 모든 경력 조회
     * (User 엔티티를 거치지 않고 외래키 ID로 바로 조회하여 성능 최적화)
     */
    @Query("select uc from UserCareer uc join fetch uc.position where uc.user.id = :userId")
    List<UserCareer> findByUserId(@Param("userId") Long userId);

    /**
     * 특정 유저의 특정 타입(PRIMARY/SECONDARY) 경력 조회
     */
    @Query("select uc from UserCareer uc where uc.user.id = :userId and uc.careerType = :careerType")
    Optional<UserCareer> findByUserIdAndCareerType(@Param("userId") Long userId, @Param("careerType") CareerType careerType);

    /**
     * 유저 ID로 전체 삭제 (벌크 삭제)
     */
    @Modifying
    @Query("delete from UserCareer uc where uc.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}