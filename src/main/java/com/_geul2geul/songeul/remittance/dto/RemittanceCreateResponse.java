package com._geul2geul.songeul.remittance.dto;

import com._geul2geul.songeul.remittance.domain.Remittance;
import com._geul2geul.songeul.remittance.domain.RemittanceImage;
import com._geul2geul.songeul.remittance.domain.RemittanceStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RemittanceCreateResponse {

    private Long remittanceId;
    private Long imageId;
    private RemittanceStatus status;

    public static RemittanceCreateResponse of(Remittance remittance, RemittanceImage image) {
        return RemittanceCreateResponse.builder()
                .remittanceId(remittance.getId())
                .imageId(image.getId())
                .status(remittance.getStatus())
                .build();
    }

}
