package com._geul2geul.songeul.remittance.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceUpdateRequest {

    private String recipientName;
    private String bankName;
    private String accountNumber;

    @Positive(message = "송금액은 0보다 큰 숫자여야 합니다")
    private Long amount;

}
