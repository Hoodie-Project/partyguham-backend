package com.partyguham.auth.oauth.props;

import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class OauthProviderProps {
    private String clientId; private String clientSecret;
    private String redirectUri; private String authUri; private String tokenUri; private String userinfoUri;
}