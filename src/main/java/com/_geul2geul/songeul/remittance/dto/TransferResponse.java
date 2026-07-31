package com._geul2geul.songeul.remittance.dto;

import com._geul2geul.songeul.remittance.domain.Transfer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TransferResponse {

    private Long transferId;
    private Long remittanceId;
    private String recipientName;
    private String bankName;
    private String accountNumber;
    private Long amount;
    private LocalDateTime transferredAt;

    public static TransferResponse of (Transfer transfer) {
        return TransferResponse.builder()
                .transferId(transfer.getId())
                .remittanceId(transfer.getRemittance().getId())
                .recipientName(transfer.getRecipientName())
                .bankName(transfer.getBankName())
                .accountNumber(transfer.getAccountNumber())
                .amount(transfer.getAmount())
                .transferredAt(transfer.getTransferredAt())
                .build();
    }

}
