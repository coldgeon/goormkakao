package com.kakaologin.goormkakao.common.exception;

import com.kakaologin.goormkakao.common.code.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException e) {
        ApiResponse<Object> response = ApiResponse.onFailure(e.getCode());
        return new ResponseEntity<>(response, e.getCode().getReasonHttpStatus().getHttpStatus());
    }
}
