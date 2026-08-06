package com._geul2geul.songeul.remittance.dto;

import com._geul2geul.songeul.remittance.domain.OcrFieldMeta;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OcrFieldResponse {

    private Object value;
    private Double confidence;
    private boolean needsConfirmation;

    public static OcrFieldResponse of(Object value, OcrFieldMeta meta) {
        return OcrFieldResponse.builder()
                .value(value)
                .confidence(meta != null ? meta.getConfidence() : null)
                .needsConfirmation(meta != null && meta.isNeedsConfirmation())
                .build();
    }

}
