package com.partyguham.config;

import com.partyguham.auth.filter.AuthExceptionFilter;
import com.partyguham.auth.jwt.JwtAuthFilter;
import com.partyguham.auth.ott.security.OttAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthExceptionFilter authExceptionFilter;
    private final OttAuthFilter ottAuthFilter;
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * 🔹 1번 체인: /api/** 전용 (JWT + OTT, stateless)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")          // 이 체인은 /api/** 만 적용
                .csrf(AbstractHttpConfigurer::disable)        // REST API 이므로 CSRF OFF
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        /* ==== 공개 테스트 API ==== */
                        .requestMatchers(
                                "/api/v2/auth/**",
                                "/api/v2/banners",
                                "/api/v2/locations",
                                "/api/v2/personalities",
                                "/api/v2/positions",
                                "/api/v2/auth/oauth/**"
                        ).permitAll()

                        /* ==== 파티 관련 공개 API (인증 불필요) ==== */
                        .requestMatchers(HttpMethod.GET, "/api/v2/parties").permitAll()                    // 파티 목록 조회
                        .requestMatchers(HttpMethod.GET, "/api/v2/parties/{partyId}").permitAll()         // 파티 단일 조회
                        .requestMatchers(HttpMethod.GET, "/api/v2/parties/{partyId}/users").permitAll()   // 파티원 목록 조회
                        .requestMatchers(HttpMethod.GET, "/api/v2/parties/types").permitAll()             // 파티 타입 목록 조회
                        .requestMatchers(HttpMethod.GET, "/api/v2/parties/search").permitAll()            // 파티 검색
                        /* ==== 모집공고 관련 공개 API (인증 불필요) ==== */
                        .requestMatchers(HttpMethod.GET, "/api/v2/parties/{partyId}/recruitments").permitAll() // 파팀 모집 목록 조회
                        .requestMatchers(HttpMethod.GET, "/api/v2/parties/{partyId}/recruitments/{partyRecruitmentId}").permitAll() // 파티 모집 단일 조회
                        .requestMatchers(HttpMethod.GET, "/api/v2/parties/admin/{partyId}/recruitments/batch-status").permitAll()

                        // OTT로 보호할 엔드포인트
                        .requestMatchers("/api/v2/users/check-nickname")
                        .hasRole("SIGNUP")

                        .requestMatchers("/api/v2/users/recover/**")
                        .hasRole("RECOVER")

                        /* ==== JWT 필요한 엔드포인트 ==== */
                        .requestMatchers("/api/v2/**").authenticated()

                        // 나머지 /api/** 는 JWT 인증 필요
                        .anyRequest().authenticated()
                );

        http
                // 3. JWT 필터를 표준 필터 앞에 등록
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // 2. OTT 필터를 JWT 필터 앞에 등록
                .addFilterBefore(ottAuthFilter, JwtAuthFilter.class)
                // 1. 에러 필터를 OTT 필터 앞에 등록
                .addFilterBefore(authExceptionFilter, OttAuthFilter.class);

        http.cors(Customizer.withDefaults());

        return http.build();
    }

    /**
     * 🔹 2번 체인: /admin/** 전용 (폼 로그인, stateful)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain adminChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")       // 🔥 이 체인은 /admin/** 만 적용
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin/signin")
                        .defaultSuccessUrl("/admin", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                        .permitAll()
                );

        // admin은 세션/폼 기반이라 CSRF 기본 ON 유지
        http.csrf(Customizer.withDefaults());

        return http.build();
    }

    // 🔹 (선택) 그 외 경로용 체인: 다 permitAll
    // 필요 없으면 생략해도 됨
    @Bean
    @Order(3)
    public SecurityFilterChain otherChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // 🔹 관리자 테스트 계정
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