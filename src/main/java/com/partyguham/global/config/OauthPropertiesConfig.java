package com.partyguham.global.config;

import com.partyguham.domain.auth.oauth.props.OauthProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * oauth.* YML 설정을 OauthProps 클래스와 연결해주는 설정 파일
 */
@Configuration
@EnableConfigurationProperties(OauthProps.class)
public class OauthPropertiesConfig { }