package com.partyguham.infra.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties props;

    @Bean
    public S3Client s3Client() {

        AwsBasicCredentials creds = AwsBasicCredentials.create(
                props.getCredentials().getAccessKey(),
                props.getCredentials().getSecretKey()
        );

        return S3Client.builder()
                .region(Region.of(props.getS3().getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();
    }
}