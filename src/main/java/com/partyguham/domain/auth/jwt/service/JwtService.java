package com.partyguham.domain.auth.jwt.service;

import com.partyguham.domain.auth.jwt.JwtProperties;
import com.partyguham.global.exception.BusinessException;
import com.partyguham.infra.redis.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static com.partyguham.domain.auth.exception.AuthErrorCode.INVALID_REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties props;
    private final RedisService redisService;

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
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(props.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(props.getRefreshExp())))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();

        redisService.saveRefreshToken(userId, refreshToken, Duration.ofMillis(props.getRefreshExp()));

        return refreshToken;
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
        // 1. JWT 파싱 및 기본 검증
        var claims = parse(refreshToken).getPayload();
        Long userId = Long.valueOf(claims.getSubject());

        // 2. Redis에 저장된 토큰과 비교
        String savedToken = redisService.getRefreshToken(userId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        // 3. (선택사항) 실제 유저가 존재하는지, 차단된 유저가 아닌지 확인
        // User user = userRepository.findById(userId).orElseThrow(...);
        // String role = user.getRole().name()

        String role = "USER";
        return issueAccess(userId, role);
    }

    /** 로그아웃: 리프레시 토큰 무효화 */
    public void revokeRefresh(String refreshToken) {
        try {
            // 1. JWT 파싱 및 기본 검증 (서명 불일치나 형식이 틀리면 Exception 발생)
            var claims = parse(refreshToken).getPayload();
            String userIdStr = claims.getSubject();

            if (userIdStr != null) {
                Long userId = Long.valueOf(userIdStr);
                // 2. Redis에서 해당 유저의 RT 삭제
                redisService.deleteRefreshToken(userId);
            }
        } catch (Exception e) {
            // 로그아웃 시 토큰이 이미 만료되었거나 유효하지 않아도
            // 서버 입장에서는 '이미 무효화된 상태'와 같으므로 조용히 처리합니다.
            log.info("이미 만료되었거나 잘못된 토큰으로 로그아웃 시도: {}", e.getMessage());
        }
    }
}