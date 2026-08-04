package com._geul2geul.songeul.remittance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(example = "박영수")
    private String recipientName;

    @Schema(example = "국민은행")
    private String bankName;

    @Schema(example = "123-456-789012")
    private String accountNumber;

    @Schema(example = "50000")
    @Positive(message = "송금액은 0보다 큰 숫자여야 합니다")
    private Long amount;

}
