package com.kakaologin.goormkakao.jwt;

import com.kakaologin.goormkakao.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-time}") long expirationTime) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    /**
     * 사용자 정보를 받아 JWT를 생성합니다.
     * @param user 로그인한 사용자 정보
     * @return 생성된 JWT 문자열
     */
    public String createToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // JWT의 subject에는 사용자를 식별할 수 있는 고유한 값을 넣는 것이 좋습니다.
        // 여기서는 User 테이블의 ID(PK)를 사용합니다.
        return Jwts.builder()
                .subject(String.valueOf(user.getId())) // 토큰의 주체 (사용자 ID)
                .claim("nickname", user.getNickname()) // 비공개 클레임 (부가 정보)
                .issuedAt(now) // 토큰 발급 시간
                .expiration(expiryDate) // 토큰 만료 시간
                .signWith(secretKey) // 서명에 사용할 비밀키
                .compact();
    }
}
