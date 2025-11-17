package com.partyguham.auth.oauth.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "oauth")
public class OauthProps {
    private OauthProviderProps kakao;
    private OauthProviderProps google;
}