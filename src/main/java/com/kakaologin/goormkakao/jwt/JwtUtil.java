package com.kakaologin.goormkakao.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String userId, String role) {
        return createToken(userId, role, accessTokenExpirationTime);
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, null, refreshTokenExpirationTime);
    }

    /**
     * Claims를 별도로 생성하지 않고, JwtBuilder 체인 안에서 모든 것을 처리하도록 수정합니다.
     */
    private String createToken(String userId, String role, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // JwtBuilder를 생성합니다.
        JwtBuilder builder = Jwts.builder()

                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey);

        // role이 null이 아닐 경우에만 claim을 추가합니다.
        if (role != null) {
            builder.claim("role", role);
        }

        // 최종적으로 토큰을 생성합니다.
        return builder.compact();
    }

    public Claims getPayload(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("토큰이 만료되었습니다: {}", token, e);
            throw e;
        } catch (SignatureException e) {
            log.error("토큰 서명이 유효하지 않습니다: {}", token, e);
            throw e;
        } catch (Exception e) {
            log.error("토큰 파싱 중 알 수 없는 오류가 발생했습니다: {}", token, e);
            throw new RuntimeException("유효하지 않은 토큰입니다.", e);
        }
    }
}
