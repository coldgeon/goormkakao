package com.kakaologin.goormkakao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaologin.goormkakao.dto.KakaoTokenResponse;
import com.kakaologin.goormkakao.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper; // ObjectMapper 주입

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // 반환 타입을 KakaoTokenResponse DTO로 변경
    public KakaoTokenResponse getAccessToken(String code) throws Exception {
        String reqURL = "https://kauth.kakao.com/oauth/token";
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // POST 요청 설정
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // 파라미터 작성
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        String sb = "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&code=" + code;
        bw.write(sb);
        bw.flush();

        // 응답 코드 확인
        int responseCode = conn.getResponseCode();
        System.out.println("responseCode : " + responseCode);

        // 응답 메시지 읽기
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        StringBuilder result = new StringBuilder();

        while ((line = br.readLine()) != null) {
            result.append(line);
        }

        br.close();
        bw.close();
        // JSON 문자열을 KakaoTokenResponse DTO 객체로 변환
        return objectMapper.readValue(result.toString(), KakaoTokenResponse.class);
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) throws Exception {
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        return objectMapper.readValue(result.toString(), KakaoUserInfoResponse.class);
    }

    // 파라미터 타입을 KakaoUserInfoResponse DTO로 변경
    @Transactional
    public User registerUserIfNeeded(KakaoUserInfoResponse userInfo) {
        long kakaoId = userInfo.getId();
        Optional<User> existingUser = userRepository.findByKakaoId(kakaoId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        // DTO 객체에서 직접 값을 가져오므로 코드가 훨씬 깔끔해짐
        String nickname = userInfo.getKakaoAccount().getProfile().getNickname();
        String email = userInfo.getKakaoAccount().getEmail();

        User newUser = User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .email(email != null ? email : "")
                .build();
        return userRepository.save(newUser);
    }

}
