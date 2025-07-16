package com.kakaologin.goormkakao.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;
    private Long refreshExpiresIn;

    @Builder
    public AuthTokens(String accessToken, String refreshToken, String grantType, Long expiresIn, Long refreshExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
    }
}
