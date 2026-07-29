package com._geul2geul.songeul.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-1234-5678 형식이어야 합니다.")
    private String phoneNumber;

    @NotBlank (message = "비밀번호를 입력해주세요.")
    private String password;

}
