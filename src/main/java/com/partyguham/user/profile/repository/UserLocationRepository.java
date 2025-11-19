package com.partyguham.user.profile.repository;


import com.partyguham.user.profile.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

    List<UserLocation> findByUserId(Long userId);

    boolean existsByUserIdAndLocationId(Long userId, Long locationId);

    long countByUserId(Long userId);

    void deleteByUserIdAndLocationId(Long userId, Long locationId);
}