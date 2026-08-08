package com._geul2geul.songeul.remittance.dto;

import com._geul2geul.songeul.remittance.domain.Remittance;
import com._geul2geul.songeul.remittance.domain.RemittanceStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OcrResultResponse {

    private Long remittanceId;
    private RemittanceStatus status;
    private OcrFieldsResponse fields;

    public static OcrResultResponse of(Remittance remittance) {
        boolean recognized = remittance.getStatus() == RemittanceStatus.OCR_COMPLETED
                || remittance.getStatus() == RemittanceStatus.COMPLETED;

        return OcrResultResponse.builder()
                .remittanceId(remittance.getId())
                .status(remittance.getStatus())
                .fields(recognized ? OcrFieldsResponse.of(remittance) : null)
                .build();
    }

    @Getter
    @Builder
    public static class OcrFieldsResponse {

        private OcrFieldResponse name;
        private OcrFieldResponse bank;
        private OcrFieldResponse account;
        private OcrFieldResponse amount;

        public static OcrFieldsResponse of(Remittance remittance) {
            return OcrFieldsResponse.builder()
                    .name(OcrFieldResponse.of(remittance.getRecipientName(), remittance.getNameMeta()))
                    .bank(OcrFieldResponse.of(remittance.getBankName(), remittance.getBankMeta()))
                    .account(OcrFieldResponse.of(remittance.getAccountNumber(), remittance.getAccountMeta()))
                    .amount(OcrFieldResponse.of(remittance.getAmount(), remittance.getAmountMeta()))
                    .build();
        }

    }

}
