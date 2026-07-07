package com.hyperlofy.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            @Value("${app.jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
            @Value("${app.jwt.issuer}") String issuer) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
        this.issuer = issuer;
    }

    public String generateAccessToken(String email, String role, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);
        return createToken(claims, email, tokenValidityInMilliseconds);
    }

    public String generateRefreshToken(String email) {
        return createToken(new HashMap<>(), email, refreshTokenValidityInMilliseconds);
    }

    private String createToken(Map<String, Object> claims, String subject, long validity) {
        Date now = new Date();
        Date validityDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(validityDate)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT Signature or Expired Token: {}", e.getMessage());
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("userId", String.class);
    }
}
