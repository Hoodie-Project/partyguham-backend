package com.partyguham.auth.oauth.repository;


import com.partyguham.auth.oauth.entity.OauthAccount;
import com.partyguham.auth.oauth.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, Long> {

    Optional<OauthAccount> findByProviderAndExternalId(Provider provider, String externalId);
    boolean existsByUserIdAndProvider(Long userId, Provider provider);
}