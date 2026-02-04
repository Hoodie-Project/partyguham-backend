package com.partyguham.infra.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws")
public class S3Properties {

    private S3 s3;
    private Credentials credentials;

    @Getter @Setter
    public static class S3 {
        private String bucket;
        private String region;
        private String baseFolder;
    }

    @Getter @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }
}