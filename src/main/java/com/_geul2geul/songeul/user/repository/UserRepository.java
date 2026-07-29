package com._geul2geul.songeul.user.repository;

import com._geul2geul.songeul.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
}
