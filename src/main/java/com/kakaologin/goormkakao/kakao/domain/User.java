package com.kakaologin.goormkakao.kakao.domain;

import com.kakaologin.goormkakao.kakao.domain.Enum.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    // ✨ 아래 Role 필드를 새로 추가합니다.
    @Enumerated(EnumType.STRING) // DB에 Enum 이름을 문자열로 저장합니다. (e.g., "USER")
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(Long kakaoId, String nickname, String email, Role role) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.email = email;
        this.role = role; // 빌더에도 role 추가
    }
}
