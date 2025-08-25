package com.kakaologin.goormkakao.kakao.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthRequestDto {
    private String code; // 카카오 인가 코드
}
