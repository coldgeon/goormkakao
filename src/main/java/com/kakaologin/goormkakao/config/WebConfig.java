package com.kakaologin.goormkakao.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // / 로 시작하는 모든 경로에 대해
                .allowedOrigins("http://localhost:5173") // React 앱의 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메소드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 쿠키 등 자격 증명 허용
    }

    //kakaoservice의 생성자를 생성하기 위해 필요한 WebClient를 분리해서 사용한다
    @Bean
    // 여러 WebClient Bean을 구분하기 위해 이름을 지정합니다.
    @Qualifier("kauthWebClient")
    public WebClient kauthWebClient() {
        return WebClient.create("https://kauth.kakao.com");
    }

    @Bean
    @Qualifier("kapiWebClient")
    public WebClient kapiWebClient() {
        return WebClient.create("https://kapi.kakao.com");
    }
}
