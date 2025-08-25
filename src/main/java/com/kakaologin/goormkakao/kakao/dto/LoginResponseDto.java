package com.kakaologin.goormkakao.kakao.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto { // UserInfoDto -> LoginResponseDto
    private Long id;
    private String nickname;
    private String email;
    private String accessToken;  // 우리 서비스의 Access Token
    private String refreshToken; // 우리 서비스의 Refresh Token
}
