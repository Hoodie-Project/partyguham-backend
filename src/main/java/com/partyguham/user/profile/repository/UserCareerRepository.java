package com.partyguham.user.profile.repository;

import com.partyguham.user.profile.entity.CareerType;
import com.partyguham.user.profile.entity.UserCareer;
import com.partyguham.user.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCareerRepository extends JpaRepository<UserCareer, Long> {

    List<UserCareer> findByUser(User user);

    Optional<UserCareer> findByUserAndCareerType(User user, CareerType type);

    void deleteByUserId(Long userId);

    boolean existsByUserAndCareerType(User user, CareerType type);
}