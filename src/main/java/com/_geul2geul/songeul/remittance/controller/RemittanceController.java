package com._geul2geul.songeul.remittance.controller;

import com._geul2geul.songeul.common.response.ApiResponse;
import com._geul2geul.songeul.remittance.dto.TransferResponse;
import com._geul2geul.songeul.remittance.service.RemittanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Remittance", description = "송금 관련 API")
@RestController
@RequestMapping("/api/v1/remittances")
@RequiredArgsConstructor
public class RemittanceController {

    private final RemittanceService remittanceService;

    // 5) 송금 확인
    @Operation(summary = "송금 확인", description = "사용자가 확인한 송금 정보(이름, 은행, 계좌, 금액)로 송금을 실행합니다.")
    @PostMapping("/{remittanceId}/transfer")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer (@PathVariable Long remittanceId) {

        TransferResponse response = remittanceService.transfer(remittanceId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("S201", "송금이 완료되었습니다.", response));

    }

}
