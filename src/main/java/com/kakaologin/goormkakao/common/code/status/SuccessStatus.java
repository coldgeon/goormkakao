package com.kakaologin.goormkakao.common.code.status;

import com.kakaologin.goormkakao.common.code.BaseCode;
import com.kakaologin.goormkakao.common.code.dto.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    // 일반적인 성공 응답
    _OK(HttpStatus.OK, "COMMON200", "요청에 성공했습니다."),

    // Member 관련 응답 예시
    MEMBER_FOUND(HttpStatus.OK, "MEMBER200", "사용자 조회를 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .isSuccess(true)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .isSuccess(true)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
