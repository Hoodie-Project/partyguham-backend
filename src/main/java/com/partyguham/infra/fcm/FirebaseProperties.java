package com.partyguham.infra.fcm;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "firebase.service-account")
public class FirebaseProperties {
    private String path;
}