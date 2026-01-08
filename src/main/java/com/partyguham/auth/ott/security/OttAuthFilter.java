package com.partyguham.auth.ott.security;

import com.partyguham.auth.ott.service.OttService;
import com.partyguham.auth.ott.model.OttPayload;
import com.partyguham.auth.ott.model.OttType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * ✅ OTT 필터 단독 버전
 * - 여기서 바로 Redis 검증 + ROLE 부여 + SecurityContext 설정까지 처리
 * - AuthenticationManager, Provider 안 씀
 */
@Component
@RequiredArgsConstructor
public class OttAuthFilter extends OncePerRequestFilter {

    private final OttService ottService;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 이미 다른 방식으로 인증된 경우 그냥 통과
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            String typeHeader = req.getHeader("X-OTT-Type"); // SIGNUP/RECOVER/LINK
            String token = resolveToken(req);                 // Authorization Bearer or Cookie

            if (typeHeader != null && token != null) {
                try {
                    OttType type = OttType.valueOf(typeHeader);

                    // 🔥 여기서 바로 Redis 검증 + 1회용 소비
                    OttPayload payload = ottService.consume(type, token);

                    String role = switch (payload.type()) {
                        case SIGNUP -> "ROLE_SIGNUP";
                        case RECOVER -> "ROLE_RECOVER";
                        case LINK -> "ROLE_LINK";
                    };

                    var auth = new UsernamePasswordAuthenticationToken(
                            payload, // principal: OTT 페이로드
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);

                } catch (Exception e) {
                    throw new BadCredentialsException("유효하지 않거나 만료된 OTT 토큰입니다.");
                }
            }
        }

        chain.doFilter(req, res);
    }

    /** Bearer 토큰 또는 쿠키에서 추출 */
    private String resolveToken(HttpServletRequest req) {
        // 1) Authorization: Bearer xxx
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }

        // 2) Cookie: ott=<token>
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("ott".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        return null;
    }
}