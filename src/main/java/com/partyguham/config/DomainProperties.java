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
    private String devBase;

    public String signupUrl() {
        if (devBase != null && !devBase.isEmpty()) {
            return devBase + "/signup";
        }
        return base + "/signup";
    }

    public String homeUrl() {
        if (devBase != null && !devBase.isEmpty()) {
            return devBase + "/home";
        } else {
            return base + "/home";
        }
    }
}