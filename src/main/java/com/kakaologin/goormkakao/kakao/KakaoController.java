package com.kakaologin.goormkakao.kakao;

import com.kakaologin.goormkakao.kakao.dto.LoginResponseDto;
import com.kakaologin.goormkakao.kakao.dto.OAuthRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "카카오 로그인", description = "카카오 소셜 로그인 관련 API")
public class KakaoController {
    private final KakaoService kakaoService;

    // JwtUtil은 KakaoService 내부에서 사용될 것이므로 Controller에는 필요 없습니다.

    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드를 이용하여 로그인 처리 후, 서비스의 액세스/리프레시 토큰을 발급합니다.")
    @PostMapping("/kakao/login")
    public ResponseEntity<LoginResponseDto> kakaoLogin(
            @RequestBody OAuthRequestDto oAuthRequestDto) {

        // 컨트롤러는 서비스에 인가 코드를 전달하는 역할만 수행합니다.
        // 모든 로그인 비즈니스 로직은 서비스 계층에서 처리됩니다.
        LoginResponseDto responseDto = kakaoService.login(oAuthRequestDto.getCode());

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 현재 우리 코드는 카카오에서 accesstoken을 받으면 우리 서버만의 jwt로 변환을 해 클라이언트에게 뿌린다. 그리고 저장되어있는 우리 서버의 jwt토큰을 이용해 클라이언트는 api를 호출 할 수 있다.
     * => 그래서 로그아웃 또한 서버사이드가 아닌 클라이언트 사이드에서 간단하게 우리가 준 jwt를 localstorage나 쿠키에서 삭제 해버리면? => 로그아웃인걸로 간주하면 된다.
     * 이는 따로 서버의 api 호출 없어도 되고 이전에 카카오 액세스 토큰을 이용한 로그아웃 로직 자체를 없애버려도 된다.
     * but 회원탈퇴는 별개이므로 따로 api를 만들어줘야 한다.
     */
//    @Operation(summary="카카오 로그아웃", description = "카카오 계정 로그아웃을 처리합니다.")
//    @PostMapping("/kakao/logout")
//    public ResponseEntity<Map<String, Long>> kakaoLogout(
//            // @RequestParam 대신 @RequestHeader를 사용해 토큰을 받는 것이 더 안전하고 표준적입니다.
//            @Parameter(description = "카카오 액세스 토큰", required = true)
//            @RequestHeader("Authorization") String kakaoAccessToken
//    ) {
//        // "Bearer " 접두사를 제거하고 순수 토큰만 추출합니다.
//        String token = kakaoAccessToken.replace("Bearer ", "");
//        Long loggedOutUserId = kakaoService.logout(token);
//
//        // 단순 문자열 대신 구조화된 JSON 응답을 반환합니다.
//        return ResponseEntity.ok(Map.of("id", loggedOutUserId));
//    }
}
