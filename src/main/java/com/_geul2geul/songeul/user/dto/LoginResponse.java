package com._geul2geul.songeul.user.dto;

import com._geul2geul.songeul.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private Long userId;
    private String name;
    private String accessToken;

    public static LoginResponse of(User user, String accessToken) {
        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .accessToken(accessToken)
                .build();

    }
}
