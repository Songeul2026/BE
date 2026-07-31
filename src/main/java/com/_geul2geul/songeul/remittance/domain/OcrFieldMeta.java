package com._geul2geul.songeul.remittance.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Embeddable
public class OcrFieldMeta {
    private Double confidence;   // 신뢰도
    private boolean needsConfirmation;  // 확인 필요 여치
}
