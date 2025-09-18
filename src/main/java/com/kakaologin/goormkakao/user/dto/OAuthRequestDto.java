package com.kakaologin.goormkakao.user.dto;

import com.kakaologin.goormkakao.common.code.enums.Platform;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthRequestDto {
    private String code; // 플랫폼 별로 받을 인가코드
    private Platform platform; // "KAKAO" 또는 "NAVER" 값이 들어올 필드

}
