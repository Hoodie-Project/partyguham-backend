package com.partyguham.user.account.repository;


import com.partyguham.user.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);
}