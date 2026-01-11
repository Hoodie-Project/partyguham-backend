package com.partyguham.user.account.repository;


import com.partyguham.user.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNicknameIgnoreCase(String nickname);
    boolean existsByEmail(String email);
    Optional<User> findByNicknameIgnoreCase(String nickname);

    @Query("select u from User u left join fetch u.profile where u.id = :userId")
    Optional<User> findByIdWithProfile(@Param("userId") Long userId);

}