package com.kakaologin.goormkakao.common.code.status;

import com.kakaologin.goormkakao.common.code.BaseErrorCode;
import com.kakaologin.goormkakao.common.code.dto.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),

    // Member 관련 에러 예시
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "해당 ID의 사용자를 찾을 수 없습니다."),

    // JWT 관련 에러 예시
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN4001", "토큰이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder() // ErrorReasonDTO -> ReasonDTO
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder() // ErrorReasonDTO -> ReasonDTO
                .isSuccess(false)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
