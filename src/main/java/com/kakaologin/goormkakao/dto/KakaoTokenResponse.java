package com.kakaologin.goormkakao.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String grantType;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("refresh_token_expires_in")
    private int refreshTokenExpiresIn;

}
