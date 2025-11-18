package com.partyguham.auth.jwt;

import com.partyguham.auth.jwt.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 매 요청마다 Authorization 헤더에서 JWT를 꺼내
 * - 서명/만료 검증
 * - userId, role 읽어서 SecurityContext에 심는 필터
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var jws = jwtService.parse(token); // 서명 + 만료 검증
                var claims = jws.getPayload();

                Long userId = Long.valueOf(claims.getSubject());
                String role = claims.get("role", String.class); // "USER" 등

                UserPrincipal principal = new UserPrincipal(userId, role);

                var auth = new UsernamePasswordAuthenticationToken(
                        principal, // userId
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException | IllegalArgumentException e) {
                // 토큰 위조/만료/형식 오류 → 그냥 인증 없이 통과 (401은 컨트롤러에서 처리)
                // response.sendError(401) 로 바로 끊고 싶으면 여기서 처리
            }
        }

        chain.doFilter(request, response);
    }
}