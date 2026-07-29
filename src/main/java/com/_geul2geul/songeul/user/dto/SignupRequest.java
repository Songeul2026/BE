package com._geul2geul.songeul.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank (message = "이름을 입력해주세요.")
    @Size (min = 2, max = 10, message = "최소 두 글자 이상 작성해주세요.")
    private String name;

    @NotBlank (message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-1234-5678 형식이어야 합니다.")
    private String phoneNumber;

    @NotBlank (message = "비밀번호를 입력해주세요.")
    @Size(min=8, message = "비밀번호는 최소 8자 이상 이어야합니다.")
    private String password;

}
