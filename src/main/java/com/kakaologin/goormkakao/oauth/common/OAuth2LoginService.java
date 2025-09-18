package com.kakaologin.goormkakao.oauth.common;

import com.kakaologin.goormkakao.common.code.enums.Platform;
import com.kakaologin.goormkakao.user.domain.User;

public interface OAuth2LoginService {
    Platform supports();
    User toEntityUser(String code, Platform platform);
}
