package com._geul2geul.songeul.remittance.dto;

import com._geul2geul.songeul.remittance.domain.Remittance;
import com._geul2geul.songeul.remittance.domain.RemittanceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RemittanceUpdateResponse {

    private Long remittanceId;
    private String recipientName;
    private String bankName;
    private String accountNumber;
    private Long amount;
    private RemittanceStatus status;
    private LocalDateTime updatedAt;

    public static RemittanceUpdateResponse of(Remittance remittance) {
        return RemittanceUpdateResponse.builder()
                .remittanceId(remittance.getId())
                .recipientName(remittance.getRecipientName())
                .bankName(remittance.getBankName())
                .accountNumber(remittance.getAccountNumber())
                .amount(remittance.getAmount())
                .status(remittance.getStatus())
                .updatedAt(remittance.getUpdatedAt())
                .build();
    }

}
