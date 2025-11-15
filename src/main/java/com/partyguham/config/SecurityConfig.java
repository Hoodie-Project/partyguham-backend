package com.partyguham.config;

import com.partyguham.auth.jwt.JwtAuthFilter;                     // 🔥 JWT 필터
import com.partyguham.auth.ott.security.OttAuthFilter;            // 🔥 OTT 필터
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
 * 🔥 통합 Security 설정
 *
 * 보호 방식 3가지 동시 사용
 * ----------------------------------------------------
 * 1) /admin/**       → 폼 로그인(상태FUL)
 * 2) /api/v2/**      → JWT 인증(상태LESS)
 * 3) /api/v2/signup  → OTT 인증(회원가입, 복구 단계)
 *
 * OTT 인증은: 회원가입/복구 단계 보호
 * JWT 인증은: 로그인이 완료된 이후 보호
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // ========= CAPTCHA =========
    private final OttAuthFilter ottAuthFilter;
    private final OttAuthenticationProvider ottAuthenticationProvider;
    private final JwtAuthFilter jwtAuthFilter;   // JWT 인증 필터


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // -------------------------------------------------------
        // 1) CSRF
        //    /admin/** → 폼 로그인이므로 CSRF 활성화
        //    /api/**   → REST API / JWT / OTT 사용 → CSRF 비활성화
        // -------------------------------------------------------
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));


        // -------------------------------------------------------
        // 2) API 인가 규칙
        // -------------------------------------------------------
        http.authorizeHttpRequests(auth -> auth

                /* ==== [OTT 인증이 필요한 엔드포인트] ==== */
                .requestMatchers(
                        "/api/v2/users",                // 회원가입
                        "/api/v2/users/check-nickname"  // 닉네임 체크
                ).hasRole("SIGNUP")

                .requestMatchers("/api/v2/users/recover/**")
                .hasRole("RECOVER")

                /* ==== [JWT 인증이 필요한 엔드포인트] ==== */
                .requestMatchers("/api/v2/**")
                .authenticated()  // → 여기는 JWT 필터로 인증됨

                /* ==== [ADMIN - FORM 로그인 보호] ==== */
                .requestMatchers("/admin/**")
                .authenticated()

                /* ==== [그 외 공개] ==== */
                .anyRequest().permitAll()
        );


        // -------------------------------------------------------
        // 3) 관리자 영역: Form Login
        // -------------------------------------------------------
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


        // -------------------------------------------------------
        // 4) OTT 필터 (회원가입/복구용) 추가
        //    → UsernamePasswordAuthenticationFilter 이전에 실행
        // -------------------------------------------------------
        http.authenticationProvider(ottAuthenticationProvider);
        http.addFilterBefore(ottAuthFilter, UsernamePasswordAuthenticationFilter.class);


        // -------------------------------------------------------
        // 5) JWT 필터 추가
        //    → OTT 인증보다 뒤에서 실행해도 되고 앞에서도 됨
        //
        //    ※ 실무에서는 JWT 필터를 최앞단에 배치하는 경우 많음
        // -------------------------------------------------------
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        // -------------------------------------------------------
        // 6) 기본 CORS 허용
        // -------------------------------------------------------
        http.cors(Customizer.withDefaults());

        return http.build();
    }


    // ---------- ADMIN 테스트용 계정 ----------
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