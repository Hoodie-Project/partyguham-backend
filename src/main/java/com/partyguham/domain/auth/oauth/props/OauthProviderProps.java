package com.partyguham.domain.auth.oauth.props;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthProviderProps {

    private String clientId;
    private String clientSecret;
    private String authUri;
    private String tokenUri;
    private String userinfoUri;

    // login / link 두 개를 담을 하위 객체
    private Redirect redirect = new Redirect();

    @Getter
    @Setter
    public static class Redirect {
        private String login;  // 웹 로그인용 콜백
        private String link;   // 계정 연동용 콜백
    }
}