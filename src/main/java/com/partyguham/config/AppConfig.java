package com.partyguham.config;

import com.partyguham.auth.oauth.props.OauthProps;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** yml의 oauth.* 값을 POJO(OauthProps)로 바인딩 활성화 */
@Configuration
@EnableConfigurationProperties(OauthProps.class)
public class AppConfig {}