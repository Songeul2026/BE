package com._geul2geul.songeul.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;
    private Object errors;
    private OffsetDateTime timestamp;

    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return new ApiResponse<>(code, message, data, null, OffsetDateTime.now());
    }

    public static <T> ApiResponse<T> error(String code, String message, Object errors) {
        return new ApiResponse<>(code, message, null, errors, OffsetDateTime.now());
    }

}
