package com.partyguham.auth.service;

import com.partyguham.auth.JwtProperties;
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

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token);
    }
}