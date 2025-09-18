package com.kakaologin.goormkakao.user.controller;

import com.kakaologin.goormkakao.common.code.ApiResponse;
import com.kakaologin.goormkakao.user.dto.LoginResponseDto;
import com.kakaologin.goormkakao.user.dto.OAuthRequestDto;
import com.kakaologin.goormkakao.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "소셜 로그인 등 사용자 인증 관련 API") // 3. Swagger 태그 이름 변경
public class UserController {
    private final UserService userService;

    @PostMapping("/login/oauth") // 4. 통합 엔드포인트 사용
    public ApiResponse<LoginResponseDto> login(@RequestBody OAuthRequestDto request) {
        LoginResponseDto response = userService.loginByOAuth(request.getCode(), request.getPlatform());
        return ApiResponse.onSuccess(response);
    }
}
