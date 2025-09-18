package com.kakaologin.goormkakao.oauth.naver;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "naver")
@Getter
@Setter
public class NaverProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String requestTokenUri;
    private String requestProfileUri;
}
