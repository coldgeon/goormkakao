package com.kakaologin.goormkakao.common.exception;

import com.kakaologin.goormkakao.common.code.BaseErrorCode;
import com.kakaologin.goormkakao.common.code.dto.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private BaseErrorCode code;

    public ReasonDTO getErrorReason() {
        return this.code.getReason();
    }
    public ReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
