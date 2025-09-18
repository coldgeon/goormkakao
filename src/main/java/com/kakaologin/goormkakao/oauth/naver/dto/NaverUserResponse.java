package com.kakaologin.goormkakao.oauth.naver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserResponse {
    @JsonProperty("resultcode")
    private String resultCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("response")
    private NaverAccount response;

    @Getter
    @NoArgsConstructor
    //naver는 id를 스트링으로 보내준다
    public static class NaverAccount {
        private String id;
        private String email;
        private String name;
    }
}
