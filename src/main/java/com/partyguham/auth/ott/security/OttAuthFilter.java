package com.partyguham.auth.ott.security;

import com.partyguham.auth.ott.model.OttType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ✅ 요청에서 OTT 토큰을 읽고 SecurityContext에 "인증 전 토큰"을 넣어주는 필터
 *
 * - OTT Type:   X-OTT-Type 헤더에서 읽음 (SIGNUP / RECOVER / LINK)
 * - OTT Token:  Authorization(Bearer) 또는 Cookie 에서 읽음
 *
 * 이 필터가 OttAuthenticationToken을 만들어주면 → Provider가 실제 인증을 수행함.
 * 이 필터는 JwtFilter 같은 다른 인증 필터보다 먼저 추가하여 OTT 기반 인증을 가능하게 한다.
 */
@Component
public class OttAuthFilter extends OncePerRequestFilter {

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 이미 인증된 사용자면 넘어감
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            // 1) OTT 타입 읽기
            String typeHeader = req.getHeader("X-OTT-Type");  // SIGNUP 등

            // 2) OTT 토큰 읽기 (Bearer or Cookie)
            String ott = resolveToken(req);

            // 둘 다 있어야 검증 시작
            if (typeHeader != null && ott != null) {
                // OttType enum으로 변환 (valueOf는 예외 던질 수 있음 — 필요하면 안전 처리)
                OttType type = OttType.valueOf(typeHeader);

                // 인증 전 토큰 생성: Provider가 이 토큰을 받아 검증 수행
                SecurityContextHolder.getContext().setAuthentication(
                        new OttAuthenticationToken(type, ott)
                );
            }
        }

        chain.doFilter(req, res);
    }

    /**
     * ✅ Authorization: Bearer + 쿠키에서 ott 토큰을 추출하는 메서드
     * Authorization: Bearer {token} 또는 Cookie 'ott' 우선순위로 추출
     */
    private String resolveToken(HttpServletRequest req) {

        // 1) Authorization 헤더
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }

        // 2) Cookie
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