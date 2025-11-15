package com.partyguham.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties props;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.getSecret()));
    }

    /** Access Token 발급 */
    public String issueAccess(Long userId, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuer(props.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(props.getAccessExp())))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    /** Refresh Token 발급 */
    public String issueRefresh(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(props.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(props.getRefreshExp())))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    /** JWT 검증 및 Claims 파싱 */
    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token);
    }

    /** Refresh Token → Access Token 재발급 */
    public String reissueAccess(String refreshToken) {
        var claims = parse(refreshToken).getPayload();
        Long userId = Long.valueOf(claims.getSubject());

        // refresh token에는 보통 role을 포함 안하지만
        // 필요하면 UserService로 다시 가져옴
        String role = "USER";

        return issueAccess(userId, role);
    }

    /** 계정 복구/비번 재설정 토큰은 랜덤 불투명 토큰(opaque) **/
}