package com._geul2geul.songeul.remittance.domain;

import lombok.Getter;

public enum RemittanceStatus {
    OCR_PROCESSING, // OCR 처리중
    OCR_COMPLETED,  // OCR 완료 (확인/수정 가능)
    OCR_FAILED,  // OCR 실패
    COMPLETED  // 송금 완료
}
