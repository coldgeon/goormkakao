package com.kakaologin.goormkakao;

import com.kakaologin.goormkakao.dto.AuthTokens;
import com.kakaologin.goormkakao.dto.KakaoTokenResponse;
import com.kakaologin.goormkakao.dto.KakaoUserInfoResponse;
import com.kakaologin.goormkakao.dto.UserInfoDto;
import com.kakaologin.goormkakao.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "카카오 로그인", description = "카카오 소셜 로그인 관련 API")
public class KakaoController {
    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil; // JwtUtil 주입

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

//  테스트용 타임리프로 대강 만든 로그인 화면!!
//    @GetMapping("/")
//    public String home(Model model) {
//        return "home";
//    }
//    @GetMapping("/login")
//    public String login(Model model) {
//        model.addAttribute("kakaoClientId",clientId);
//        model.addAttribute("kakaoRedirectUri",redirectUri);
//        return "login"; // templates/login.html
//    }

    // 2. 카카오 로그인 콜백 처리
    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드를 이용하여 로그인 처리 후, 카카오 서버 토큰 및 유저 정보를 반환합니다.")
    @PostMapping("/kakao/login")
    public ResponseEntity<UserInfoDto> kakaoLogin(
            @Parameter(description = "카카오 서버로부터 받은 인가 코드", required = true)
            @RequestParam String code) throws Exception {
        // 1. 인가 코드로 카카오 토큰 access token 정보 받기 == json 안에서 자바로 accesstoken을 파싱하는 클래스인 KakaoTokenResponse
        KakaoTokenResponse kakaoToken = kakaoService.getAccessToken(code);

        // 2. 액세스 토큰으로 사용자 정보 받기 == 유저 정보 파싱하는 클래스인 유저인포리스폰스
        KakaoUserInfoResponse userInfoJson = kakaoService.getUserInfo(kakaoToken.getAccessToken());

        // 3. 사용자 정보로 회원가입 또는 로그인 처리
        User user = kakaoService.registerUserIfNeeded(userInfoJson);

        System.out.println(user);
        // 4. 클라이언트에 전달할 최종 DTO 생성
        
        AuthTokens authTokens = AuthTokens.builder()
                .accessToken(kakaoToken.getAccessToken())
                .refreshToken(kakaoToken.getRefreshToken())
                .grantType(kakaoToken.getGrantType())
                .expiresIn(Long.valueOf(kakaoToken.getExpiresIn()))
                .refreshExpiresIn(Long.valueOf(kakaoToken.getRefreshTokenExpiresIn()))
                .build();

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .authTokens(authTokens)
                .build();
        // 5. DTO를 ResponseEntity에 담아 반환
        return ResponseEntity.ok(userInfoDto);
    }
}
