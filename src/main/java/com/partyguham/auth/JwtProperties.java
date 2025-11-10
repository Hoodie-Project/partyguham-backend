package com.partyguham.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private String issuer;
    private long accessExp;
    private long refreshExp;
    private String header;
    private String prefix;
}