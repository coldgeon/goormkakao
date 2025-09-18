package com.kakaologin.goormkakao.user.repository;

import com.kakaologin.goormkakao.common.code.enums.Platform;
import com.kakaologin.goormkakao.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPlatformAndPlatformId(Platform platform, String platformId);
}
