package com.partyguham.config;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** RestTemplate 대신 현대 표준 WebClient 사용 */
@Configuration
public class WebClientConfig implements WebMvcConfigurer {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // URL 파라미터, PathVariable의 Enum 대소문자를 무시해줍니다.
        ApplicationConversionService.configure(registry);
    }
}