package com.kakaologin.goormkakao.oauth.naver.service;

import com.kakaologin.goormkakao.common.code.enums.Platform;
import com.kakaologin.goormkakao.oauth.common.OAuth2LoginService;
import com.kakaologin.goormkakao.user.domain.User;
import com.kakaologin.goormkakao.oauth.naver.NaverProperties;
import com.kakaologin.goormkakao.oauth.naver.dto.NaverTokenResponse;
import com.kakaologin.goormkakao.oauth.naver.dto.NaverUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NaverLoginService implements OAuth2LoginService {

    private final NaverProperties naverProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Platform supports() {
        return Platform.NAVER;
    }

    @Override
    public User toEntityUser(String code, Platform platform) {
        String accessToken = toRequestAccessToken(code);
        NaverUserResponse.NaverAccount profile = toRequestProfile(accessToken);

        // 네이버는 state 값을 추가로 요구하므로, 실제 서비스에서는 이 부분도 처리해야 합니다.
        // 여기서는 간단한 예시로 생략합니다.

        return User.builder()
                .platform(Platform.NAVER) // ✨ 1. 플랫폼 정보(NAVER)를 명시적으로 추가
                .platformId(profile.getId().toString()) // ✨ 2. 네이버 고유 ID를 platformId에 추가
                .email(profile.getEmail())
                .nickname(profile.getName())
                // 'role'은 UserService에서 신규 가입 시 설정하므로 여기서는 제외합니다.
                .build();

    }

    private String toRequestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverProperties.getClientId());
        params.add("client_secret", naverProperties.getClientSecret());
        params.add("redirect_uri", naverProperties.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        NaverTokenResponse response = restTemplate.postForObject(
                naverProperties.getRequestTokenUri(),
                request,
                NaverTokenResponse.class
        );

        return response.getAccessToken();
    }

    private NaverUserResponse.NaverAccount toRequestProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        NaverUserResponse response = restTemplate.postForObject(
                naverProperties.getRequestProfileUri(),
                request,
                NaverUserResponse.class
        );

        return response.getResponse();
    }
}
