package com.partyguham.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.domain")
public class DomainProperties {
    private String base;

    public String signupUrl() {
        return base + "/signup";
    }
    public String homeUrl() {return base + "/home";}


}