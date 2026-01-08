package com.partyguham.auth.jwt;

import com.partyguham.auth.exception.AuthErrorCode;
import com.partyguham.auth.jwt.service.JwtService;
import com.partyguham.common.error.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
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
 * JWT 필터
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

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

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
            // 인증 성공시 컨텍스트 저장
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 다음 필터로 진행
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new UnauthorizedException(AuthErrorCode.INVALID_TOKEN);
        }

    }
}