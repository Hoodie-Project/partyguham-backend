package com.partyguham.auth.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;

@Configuration
@RequiredArgsConstructor
public class JwtDecoderConfig {

    @Bean("googleJwtDecoder")
    public JwtDecoder googleJwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .build();
    }

    @Bean("kakaoJwtDecoder")
    public JwtDecoder kakaoJwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri("https://kauth.kakao.com/.well-known/jwks.json")
                .build();
    }
}