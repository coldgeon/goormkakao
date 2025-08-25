package com.kakaologin.goormkakao.kakao;

import com.fasterxml.jackson.databind.JsonNode;
import com.kakaologin.goormkakao.kakao.domain.Enum.Role;
import com.kakaologin.goormkakao.kakao.domain.User;
import com.kakaologin.goormkakao.kakao.repository.UserRepository;
import com.kakaologin.goormkakao.jwt.JwtUtil;
import com.kakaologin.goormkakao.kakao.dto.KakaoTokenResponse;
import com.kakaologin.goormkakao.kakao.dto.KakaoUserInfoResponse;
import com.kakaologin.goormkakao.kakao.dto.LoginResponseDto;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoService {

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

    @Transactional
    public LoginResponseDto login(String code) {
        // 1. 인가 코드로 카카오의 액세스 토큰 정보 받기
        KakaoTokenResponse kakaoToken = getAccessToken(code);

        // 2. 액세스 토큰으로 카카오 사용자 정보 받기
        KakaoUserInfoResponse userInfo = getUserInfo(kakaoToken.getAccessToken());

        // 3. 사용자 정보로 DB에서 가입 여부 확인 및 처리
        User user = registerUserIfNeeded(userInfo);

        // 4. 우리 서비스의 자체 JWT 생성
        String appAccessToken = jwtUtil.createAccessToken(user.getId().toString(), user.getRole().toString()); // 예시
        String appRefreshToken = jwtUtil.createRefreshToken(user.getId().toString()); // 예시

        // 5. 최종 응답 DTO 생성하여 반환
        return LoginResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .accessToken(appAccessToken)
                .refreshToken(appRefreshToken)
                .build();
    }

    /**
     * 인가 코드를 사용하여 액세스 토큰을 발급받습니다.
     */
    public KakaoTokenResponse getAccessToken(String code) {
        String uri = "/oauth/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        // WebClient를 사용하여 POST 요청을 보냅니다.
        return kauthWebClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED) // Content-Type 지정
                .body(BodyInserters.fromFormData(formData)) // body 데이터 설정
                .retrieve() // 응답을 받습니다.
                .bodyToMono(KakaoTokenResponse.class) // 응답 본문을 DTO로 변환합니다.
                .block(); // 비동기 처리를 동기적으로 완료하고 결과를 받습니다.
    }

    /**
     * 액세스 토큰을 사용하여 사용자 정보를 조회합니다.
     */
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        String uri = "/v2/user/me";

        return kapiWebClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken) // 헤더에 토큰 추가
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();
    }

    /**
     * 카카오 ID를 기반으로 사용자를 조회하고, 없으면 새로 등록합니다.
     * (이 메소드는 외부 API 호출이 없으므로 변경되지 않습니다.)
     */
    @Transactional
    public User registerUserIfNeeded(KakaoUserInfoResponse userInfo) {
        long kakaoId = userInfo.getId();
        Optional<User> existingUser = userRepository.findByKakaoId(kakaoId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        String nickname = userInfo.getKakaoAccount().getProfile().getNickname();
        String email = userInfo.getKakaoAccount().getEmail();

        User newUser = User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .email(email != null ? email : "")
                .role(Role.USER) // ✨ 신규 가입 시 기본으로 USER 역할을 부여합니다.
                .build();

        return userRepository.save(newUser);
    }

    /**
     * 액세스 토큰을 사용하여 로그아웃을 처리합니다.
     */
//    public Long logout(String accessToken) {
//        String uri = "/v1/user/logout";
//
//        // 로그아웃 요청을 보내고 응답을 JsonNode로 받습니다.
//        JsonNode response = kapiWebClient.post()
//                .uri(uri)
//                .header("Authorization", "Bearer " + accessToken)
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .block();
//
//        // 응답에서 로그아웃된 사용자의 id를 추출합니다.
//        if (response != null && response.has("id")) {
//            long id = response.get("id").asLong();
//            log.info("Logout successful for user ID: {}", id);
//            return id;
//        } else {
//            // 예외를 발생시키거나 로그를 남겨 실패를 알립니다.
//            throw new RuntimeException("Failed to process Kakao logout.");
//        }
//    }
}
