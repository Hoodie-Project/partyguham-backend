package com.partyguham.infra.fcm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "firebase.service-account")
public class FirebaseProperties {
    private String path;
}