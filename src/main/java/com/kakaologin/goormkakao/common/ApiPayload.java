package com.kakaologin.goormkakao.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiPayload<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final ErrorResponse error;

    // --- 성공 응답을 생성하는 정적 팩토리 메소드 ---

    /**
     * 데이터와 성공 메시지를 포함하는 성공 응답을 생성합니다.
     * @param data 포함할 데이터
     * @param message 성공 메시지
     * @return ApiPayload 인스턴스
     */
    public static <T> ApiPayload<T> of(T data, String message) {
        return new ApiPayload<>(true, message, data, null);
    }

    /**
     * 데이터를 포함하는 성공 응답을 생성합니다. (기본 성공 메시지 사용)
     * @param data 포함할 데이터
     * @return ApiPayload 인스턴스
     */
    public static <T> ApiPayload<T> of(T data) {
        return of(data, "요청에 성공했습니다.");
    }

    /**
     * 데이터 없이 성공 메시지만 포함하는 성공 응답을 생성합니다. (주로 CUD 작업에 사용)
     * @param message 성공 메시지
     * @return ApiPayload 인스턴스
     */
    public static <T> ApiPayload<T> success(String message) {
        return new ApiPayload<>(true, message, null, null);
    }

    // --- 실패 응답을 생성하는 정적 팩토리 메소드 ---

    /**
     * 에러 코드와 에러 메시지를 포함하는 실패 응답을 생성합니다.
     * @param code 에러 코드 (e.g., "USER_NOT_FOUND")
     * @param message 에러 메시지
     * @return ApiPayload 인스턴스
     */
    public static <T> ApiPayload<T> error(String code, String message) {
        return new ApiPayload<>(false, null, null, new ErrorResponse(code, message));
    }


    /**
     * 에러 정보를 담는 내부 레코드(Record) 클래스입니다.
     * Java 16+에서 사용 가능하며, 불변 데이터를 간결하게 표현할 수 있습니다.
     */
    public record ErrorResponse(String code, String message) {}
}
