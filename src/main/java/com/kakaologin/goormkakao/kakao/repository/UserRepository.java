package com.kakaologin.goormkakao.kakao.repository;

import com.kakaologin.goormkakao.kakao.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(Long kakaoId);
}
