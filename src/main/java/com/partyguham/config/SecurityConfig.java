package com.partyguham.config;

import com.partyguham.auth.jwt.JwtAuthFilter;
import com.partyguham.auth.ott.security.OttAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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

    private final OttAuthFilter ottAuthFilter;
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * 🔹 1번 체인: /api/** 전용 (JWT + OTT, stateless)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")          // 🔥 이 체인은 /api/** 만 적용
                .csrf(csrf -> csrf.disable())        // REST API 이므로 CSRF OFF
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        /* ==== 공개 테스트 API ==== */
                        .requestMatchers(
                                "/api/v2/auth/test/**",
                                "/api/v2/health",
                                "/api/v2/auth/oauth/**"
                        ).permitAll()

                        // OTT로 보호할 엔드포인트
                        .requestMatchers("/api/v2/users/recover/**")
                        .hasRole("RECOVER")

                        /* ==== JWT 필요한 엔드포인트 ==== */
                        .requestMatchers("/api/v2/**").authenticated()

                        // 나머지 /api/** 는 JWT 인증 필요
                        .anyRequest().authenticated()
                );

        // OTT 인증
        http.addFilterBefore(ottAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // JWT 인증
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

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