package com.kakaologin.goormkakao.common.code;

import com.kakaologin.goormkakao.common.code.dto.ReasonDTO;

public interface BaseErrorCode extends BaseCode{
    // BaseCode의 메소드를 오버라이딩하여 반환 타입을 ErrorReasonDTO로 구체화합니다.
    @Override
    ReasonDTO getReason();

    @Override
    ReasonDTO getReasonHttpStatus();
}
