package com._geul2geul.songeul.user.controller;

import com._geul2geul.songeul.common.response.ApiResponse;
import com._geul2geul.songeul.user.dto.SignupRequest;
import com._geul2geul.songeul.user.dto.SignupResponse;
import com._geul2geul.songeul.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    @Operation(summary = "회원가입", description = "전화번호 중복 검증 후 비밀번호를 해싱하여 회원가입을 처리합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("S201", "회원가입이 완료되었습니다.", response));

    }
}
