package com.kakaologin.goormkakao.user.domain;

import com.kakaologin.goormkakao.common.code.enums.Platform;
import com.kakaologin.goormkakao.user.domain.Enum.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// platform과 platformId의 조합이 고유하도록 복합 유니크 키를 설정합니다.
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_users_platform_platformId",
                columnNames = {"platform", "platformId"}
        )
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    // email은 소셜 로그인 제공사에 따라 null일 수 있으므로 nullable=true로 설정합니다.
    @Column(nullable = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform; // 가입한 소셜 플랫폼 (KAKAO, NAVER 등)

    @Column(nullable = false)
    private String platformId; // 소셜 플랫폼에서 발급받은 고유 ID

    @Builder
    public User(String nickname, String email, Role role, Platform platform, String platformId) {
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.platform = platform;
        this.platformId = platformId;
    }
}
