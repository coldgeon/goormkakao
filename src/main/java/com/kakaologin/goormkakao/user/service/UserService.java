package com.kakaologin.goormkakao.user.service;

import com.kakaologin.goormkakao.common.code.enums.Platform;
import com.kakaologin.goormkakao.common.code.status.ErrorStatus;
import com.kakaologin.goormkakao.common.exception.GeneralException;
import com.kakaologin.goormkakao.oauth.common.OAuth2LoginService;
import com.kakaologin.goormkakao.oauth.jwt.JwtUtil;
import com.kakaologin.goormkakao.user.dto.LoginResponseDto;
import com.kakaologin.goormkakao.user.domain.Enum.Role;
import com.kakaologin.goormkakao.user.domain.User;
import com.kakaologin.goormkakao.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UserService {

    private final List<OAuth2LoginService> oAuth2LoginServices;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * OAuth 로그인을 처리하는 메인 메소드
     */
    @Transactional
    public LoginResponseDto loginByOAuth(String code, Platform platform) {
        // 1. 전달받은 platform에 맞는 OAuth2LoginService 구현체를 찾습니다. (전략 패턴)
        OAuth2LoginService oAuth2LoginService = this.oAuth2LoginServices.stream()
                .filter(service -> service.supports().equals(platform))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus._BAD_REQUEST)); // 적절한 예외 처리

        // 2. 해당 서비스로 사용자 정보를 받아와 User 엔티티로 변환합니다.
        User oauthUser = oAuth2LoginService.toEntityUser(code, platform);

        // 3. 받아온 정보로 회원을 찾거나 새로 가입시킵니다.
        User user = findOrRegisterUser(oauthUser);

        // 4. 우리 서비스의 JWT를 발급하여 DTO로 만들어 반환합니다.
        return createJwtAndBuildResponse(user);
    }

    /**
     * DB에서 사용자를 찾고, 없으면 새로 등록하는 메소드
     */
    private User findOrRegisterUser(User oauthUser) {
        return userRepository.findByPlatformAndPlatformId(oauthUser.getPlatform(), oauthUser.getPlatformId())
                .orElseGet(() -> {
                    // 새로 가입하는 사용자는 기본 Role을 부여합니다.
                    User newUser = User.builder()
                            .nickname(oauthUser.getNickname())
                            .email(oauthUser.getEmail())
                            .platform(oauthUser.getPlatform())
                            .platformId(oauthUser.getPlatformId())
                            .role(Role.USER) // 기본 역할 부여
                            .build();
                    return userRepository.save(newUser);
                });
    }

    /**
     * 최종 User 엔티티를 기반으로 JWT를 생성하고 응답 DTO를 만드는 메소드
     */
    private LoginResponseDto createJwtAndBuildResponse(User user) {
        // JWT 생성
        String accessToken = jwtUtil.createAccessToken(user.getId().toString(), user.getRole().toString());
        String refreshToken = jwtUtil.createRefreshToken(user.getId().toString());

        return LoginResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
