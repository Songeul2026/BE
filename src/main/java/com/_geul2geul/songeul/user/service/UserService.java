package com._geul2geul.songeul.user.service;

import com._geul2geul.songeul.common.exception.CustomException;
import com._geul2geul.songeul.common.exception.ErrorCode;
import com._geul2geul.songeul.user.domain.User;
import com._geul2geul.songeul.user.dto.SignupRequest;
import com._geul2geul.songeul.user.dto.SignupResponse;
import com._geul2geul.songeul.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 1) 회원가입
    public SignupResponse signup(SignupRequest request) {
        // 1. 전화 번호 중복 검증 -> 이미 존재하면 예외처리
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        // 2. 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        //3. Usr 엔티티 생성 및 저장
        User user = User.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .password(encodedPassword)
                .createdAt(LocalDateTime.now())
                .build();

        User saveUser = userRepository.save(user);

        //4. 응답 DTO로 변환해서 리턴
        return SignupResponse.from(saveUser);

    }
}
