package com.kakaologin.goormkakao.common.code;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kakaologin.goormkakao.common.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // result가 null일 경우 JSON에 포함하지 않음
    private T result;

    // --- 성공 응답 ---

    // 1. 성공 응답 (결과 데이터 포함)
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
    }

    // 2. 성공 응답 (결과 데이터와 커스텀 코드/메시지 포함)
    public static <T> ApiResponse<T> of(BaseCode code, T result) {
        return new ApiResponse<>(true, code.getReason().getCode(), code.getReason().getMessage(), result);
    }

    // --- 실패 응답 ---

    // 3. 실패 응답 (결과 데이터 없음)
    public static <T> ApiResponse<T> onFailure(BaseCode code) {
        return new ApiResponse<>(false, code.getReason().getCode(), code.getReason().getMessage(), null);
    }
}
