package com.kakaologin.goormkakao.oauth.kakao.service;

import com.kakaologin.goormkakao.common.code.enums.Platform;
import com.kakaologin.goormkakao.oauth.common.OAuth2LoginService;
import com.kakaologin.goormkakao.user.domain.User;
import com.kakaologin.goormkakao.user.repository.UserRepository;
import com.kakaologin.goormkakao.oauth.jwt.JwtUtil;
import com.kakaologin.goormkakao.oauth.kakao.dto.KakaoTokenResponse;
import com.kakaologin.goormkakao.oauth.kakao.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoLoginService implements OAuth2LoginService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil; // ✨ JwtUtil을 Controller에서 여기로 이동해주세요.


    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // @Qualifier를 통해 어떤 WebClient Bean을 주입받을지 명시합니다.
    @Qualifier("kauthWebClient")
    private final WebClient kauthWebClient;

    @Qualifier("kapiWebClient")
    private final WebClient kapiWebClient;

    @Override
    public Platform supports() {
        return Platform.KAKAO;
    }

    @Override
    public User toEntityUser(String code, Platform platform) {
        // 1. 인가 코드로 카카오 액세스 토큰 받기
        String accessToken = toRequestAccessToken(code);
        // 2. 액세스 토큰으로 사용자 정보 받기
        KakaoUserInfoResponse userInfo = toRequestProfile(accessToken);

        // 3. 받은 사용자 정보를 우리 서비스의 User 엔티티로 변환
        return User.builder()
                .platform(Platform.KAKAO) // 1. 어떤 소셜 플랫폼인지 명시합니다.
                .platformId(userInfo.getId().toString()) // 2. platformId 필드에 카카오의 고유 ID를 문자열로 저장합니다.
                .nickname(userInfo.getKakaoAccount().getProfile().getNickname())
                .email(userInfo.getKakaoAccount().getEmail())
                // 'role'은 UserService의 findOrRegisterUser에서 설정하므로 여기서는 제외합니다.
                .build();
    }
    // 카카오 서버에 액세스 토큰을 요청하는 private 메소드
    private String toRequestAccessToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        KakaoTokenResponse response = kauthWebClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        return response.getAccessToken();
    }

    // 카카오 서버에 사용자 정보를 요청하는 private 메소드
    private KakaoUserInfoResponse toRequestProfile(String accessToken) {
        return kapiWebClient.post()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();
    }
}
