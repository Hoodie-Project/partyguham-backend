package com.partyguham.domain.user.profile.repository;

import com.partyguham.domain.user.profile.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(Long userId);

    @Query("SELECT up FROM UserProfile up " +
            "JOIN FETCH up.user u " +
            "WHERE u.nickname = :nickname")
    Optional<UserProfile> findByNicknameWithUser(@Param("nickname") String nickname);
}