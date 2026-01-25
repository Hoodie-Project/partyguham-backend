package com.partyguham.global.config;

import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // JSON을 Enum으로 바꿀 때 대소문자를 구분하지 않도록 설정
            builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

            // (선택사항) Enum을 인덱스(0, 1..)가 아닌 이름(MASTER..)으로 처리하도록 명시
            // builder.featuresToDisable(DeserializationFeature.READ_ENUM_VALUES_USING_INDEX);
        };
    }
}