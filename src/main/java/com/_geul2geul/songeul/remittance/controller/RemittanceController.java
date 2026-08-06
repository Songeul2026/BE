package com._geul2geul.songeul.remittance.controller;

import com._geul2geul.songeul.common.response.ApiResponse;
import com._geul2geul.songeul.remittance.dto.OcrResultResponse;
import com._geul2geul.songeul.remittance.dto.RemittanceCreateResponse;
import com._geul2geul.songeul.remittance.dto.TransferResponse;
import com._geul2geul.songeul.remittance.service.RemittanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Remittance", description = "송금 관련 API")
@RestController
@RequestMapping("/api/v1/remittances")
@RequiredArgsConstructor
public class RemittanceController {

    private final RemittanceService remittanceService;

    // 11) OCR 결과 조회
    @Operation(summary = "OCR 결과 조회", description = "이미지 업로드 후 OCR 인식 결과를 조회합니다.")
    @GetMapping("/{remittanceId}/ocr-result")
    public ResponseEntity<ApiResponse<OcrResultResponse>> getOcrResult(@PathVariable Long remittanceId) {

        OcrResultResponse response = remittanceService.getOcrResult(remittanceId);

        String message = switch (response.getStatus()) {
            case OCR_PROCESSING -> "인식을 진행하고 있습니다";
            case OCR_COMPLETED, COMPLETED -> "인식이 완료되었습니다";
            case OCR_FAILED -> "인식에 실패했습니다. 다시 촬영해주세요";
        };

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("S200", message, response));
    // 10) 이미지 업로드 (송금 건 생성)
    @Operation(summary = "이미지 업로드 (송금 건 생성)", description = "메모지 이미지를 업로드하고 송금 건을 생성합니다. 업로드된 이미지는 OCR 인식을 위해 전달됩니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RemittanceCreateResponse>> createRemittance(
            @RequestParam("image") MultipartFile image) {

        RemittanceCreateResponse response = remittanceService.createRemittance(image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("S201", "이미지 업로드가 완료되었습니다. 인식을 진행합니다", response));

    }

    // 12) 이미지 재요청 (재촬영)
    @Operation(summary = "이미지 재요청 (재촬영)", description = "OCR 인식 실패 시 새 이미지를 업로드하여 다시 인식을 요청합니다.")
    @PostMapping(value = "/{remittanceId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RemittanceCreateResponse>> retryOcr(
            @PathVariable Long remittanceId,
            @RequestParam("image") MultipartFile image) {

        RemittanceCreateResponse response = remittanceService.retryOcr(remittanceId, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("S201", "재촬영 이미지로 다시 인식을 진행합니다", response));

    }

    // 5) 송금 확인
    @Operation(summary = "송금 확인", description = "사용자가 확인한 송금 정보(이름, 은행, 계좌, 금액)로 송금을 실행합니다.")
    @PostMapping("/{remittanceId}/transfer")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer (@PathVariable Long remittanceId) {

        TransferResponse response = remittanceService.transfer(remittanceId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("S201", "송금이 완료되었습니다.", response));

    }

    // 6) 송금 결과 조회
    @Operation(summary = "송금 결과 조회", description = "완료된 송금의 결과를 조회합니다.")
    @GetMapping("/{remittanceId}/transfer")
    public ResponseEntity<ApiResponse<TransferResponse>> getTransferResult (@PathVariable Long remittanceId) {

        TransferResponse response = remittanceService.getTransferResult(remittanceId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("S200", "송금 결과를 조회했습니다.", response));

    }

}
