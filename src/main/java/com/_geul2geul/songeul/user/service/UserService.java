package com._geul2geul.songeul.user.service;

import com._geul2geul.songeul.common.exception.CustomException;
import com._geul2geul.songeul.common.exception.ErrorCode;
import com._geul2geul.songeul.common.jwt.JwtTokenProvider;
import com._geul2geul.songeul.user.domain.User;
import com._geul2geul.songeul.user.dto.LoginRequest;
import com._geul2geul.songeul.user.dto.LoginResponse;
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
    private final JwtTokenProvider jwtTokenProvider;

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

    // 2) 로그인
    public LoginResponse login(LoginRequest request) {

        // 1. 전화 번호 조회 -> 없으면 에러 처리
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED));

        // 2. 비밀번호 일치 확인 -> 불일치면 에외처리
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 3. 토큰 발급
        String accessToken = jwtTokenProvider.generateToken(user.getId());


        // 4. 응답 DTO로 변환해서 리턴
        return LoginResponse.of(user, accessToken);

    }

}
