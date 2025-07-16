package com.kakaologin.goormkakao.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoDto {
    private Long id;
    private String nickname;
    private String email;
    private AuthTokens authTokens;

    @Builder
    public UserInfoDto(Long id, String nickname, String email, AuthTokens authTokens) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.authTokens = authTokens;
    }
}
