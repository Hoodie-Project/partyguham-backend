package com.partyguham.auth.oauth.repository;


import com.partyguham.auth.oauth.entity.OauthAccount;
import com.partyguham.auth.oauth.entity.Provider;
import com.partyguham.user.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, Long> {

    Optional<OauthAccount> findByProviderAndExternalId(Provider provider, String externalId);

    boolean existsByUserAndProvider(User user, Provider provider); // 같은 유저가 같은 provider 중복연동 방지

    List<OauthAccount> findByUserId(Long userId);
}