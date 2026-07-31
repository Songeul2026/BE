package com._geul2geul.songeul.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "E400", "비밀번호는 8자 이상이어야 합니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "E409", "이미 가입된 전화번호입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "서버 오류가 발생했습니다."),
    INVALID_TRANSFER_INFO(HttpStatus.BAD_REQUEST,"E400", "계좌번호와 금액을 모두 입력해야 송금할 수 있습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E401", "전화번호 또는 비밀번호가 올바르지 않습니다."),
    REMITTANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "송금 정보를 찾을 수 없습니다."),
    ALREADY_TRANSFERRED(HttpStatus.CONFLICT, "E409", "이미 송금이 완료된 건입니다."),
    TRANSFER_NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "송금 내역이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
