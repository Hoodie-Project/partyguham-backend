package com.partyguham.config;

import com.partyguham.auth.ott.security.OttAuthFilter;
import com.partyguham.auth.ott.security.OttAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 통합 Security 설정
 * - /admin/**: 폼 로그인(상태ful)로 보호
 * - /api/**: OTT 기반 인증(상태less에 가까움)
 * - OTT: Provider 등록 + Filter 삽입
 * - CSRF: /api/** 만 제외(폼로그인은 유지)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OttAuthFilter ottAuthFilter;                      // ✅ OTT 필터
    private final OttAuthenticationProvider ottAuthenticationProvider; // ✅ OTT 프로바이더

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1) CSRF: /api/** 는 비활성화, /admin/** 는 폼 사용을 위해 유지
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
        );

        // 2) 인가 규칙
        http.authorizeHttpRequests(auth -> auth
                // ====== API(OTT 보호) ======
                // 회원가입/닉네임 체크: SIGNUP 권한 필요
                .requestMatchers("/api/v2/users", "/api/v2/users/check-nickname").hasRole("SIGNUP")
                // 계정 복구: RECOVER 권한 필요
                .requestMatchers("/api/v2/users/recover/**").hasRole("RECOVER")
                // 소셜 연동: LINK 권한 필요 (예시)
                .requestMatchers("/api/v2/auth/oauth/link/**").hasRole("LINK")
                // ====== 관리자(폼 로그인 보호) ======
                .requestMatchers("/admin/**").authenticated()
                // 그 외 공개
                .anyRequest().permitAll()
        );

        // 3) 관리자 영역: 폼 로그인/로그아웃 그대로 유지
        http.formLogin(form -> form
                .loginPage("/admin/signin")
                .defaultSuccessUrl("/admin", true)
                .permitAll()
        );
        http.logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout")
                .permitAll()
        );

        // 4) OTT 인증 구성: Provider 등록 + Filter 삽입
        http.authenticationProvider(ottAuthenticationProvider);
        http.addFilterBefore(ottAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // (선택) 기본 CORS 적용
        http.cors(Customizer.withDefaults());

        return http.build();
    }

    // ✅ 테스트용 InMemory 계정 (관리자 폼로그인용)
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password("1234")
                        .roles("ADMIN")
                        .build()
        );
    }
}