package com._geul2geul.songeul.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "E400", "비밀번호는 8자 이상이어야 합니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "E409", "이미 가입된 전화번호입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
