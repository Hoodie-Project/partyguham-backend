package com.partyguham.auth.oauth.props;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("oauth")
public class OauthProps {
    private final OauthProviderProps kakao = new OauthProviderProps();
    private final OauthProviderProps google = new OauthProviderProps();
}