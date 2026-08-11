package com._geul2geul.songeul.remittance.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrFieldResult {

    private Object value;
    private Double confidence;
    private boolean needsConfirmation;

}
