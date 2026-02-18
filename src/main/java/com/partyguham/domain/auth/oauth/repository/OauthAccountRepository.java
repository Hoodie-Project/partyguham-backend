package com.partyguham.domain.auth.oauth.repository;


import com.partyguham.domain.auth.oauth.entity.OauthAccount;
import com.partyguham.domain.auth.oauth.entity.Provider;
import com.partyguham.domain.user.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, Long> {

    Optional<OauthAccount> findByProviderAndOauthId(Provider provider, String OauthId);

    boolean existsByUserAndProvider(User user, Provider provider); // 같은 유저가 같은 provider 중복연동 방지

    List<OauthAccount> findByUserId(Long userId);
}