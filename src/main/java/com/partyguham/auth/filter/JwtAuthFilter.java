package com.partyguham.auth.filter;

import com.partyguham.auth.JwtProperties;
import com.partyguham.auth.service.JwtService;
import jakarta.servlet.*;
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

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final JwtProperties props;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader(props.getHeader());
        String prefix = props.getPrefix() == null ? "Bearer" : props.getPrefix();
        if (header != null && header.startsWith(prefix + " ")) {
            String token = header.substring(prefix.length() + 1);
            try {
                var claims = jwt.parse(token).getPayload();
                String userId = claims.getSubject();
                String role = String.valueOf(claims.get("role"));

                var auth = new UsernamePasswordAuthenticationToken(
                        userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // 토큰 오류는 인증 없이 계속 진행(필요 시 401 반환 로직으로 변경 가능)
            }
        }
        chain.doFilter(req, res);
    }
}