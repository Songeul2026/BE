package com._geul2geul.songeul.remittance.service;

import com._geul2geul.songeul.common.exception.CustomException;
import com._geul2geul.songeul.common.exception.ErrorCode;
import com._geul2geul.songeul.common.storage.ImageStorage;
import com._geul2geul.songeul.remittance.domain.Remittance;
import com._geul2geul.songeul.remittance.domain.RemittanceImage;
import com._geul2geul.songeul.remittance.domain.RemittanceStatus;
import com._geul2geul.songeul.remittance.domain.Transfer;
import com._geul2geul.songeul.remittance.dto.OcrResultResponse;
import com._geul2geul.songeul.remittance.dto.RemittanceCreateResponse;
import com._geul2geul.songeul.remittance.dto.RemittanceUpdateRequest;
import com._geul2geul.songeul.remittance.dto.RemittanceUpdateResponse;
import com._geul2geul.songeul.remittance.dto.TransferResponse;
import com._geul2geul.songeul.remittance.repository.RemittanceImageRepository;
import com._geul2geul.songeul.remittance.repository.RemittanceRepository;
import com._geul2geul.songeul.remittance.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RemittanceService {

    // TODO: 인증(로그인) 기능 활성화 후 실제 로그인 사용자 ID로 교체
    private static final Long TEMP_USER_ID = 1L;

    private final RemittanceRepository remittanceRepository;
    private final TransferRepository transferRepository;
    private final RemittanceImageRepository remittanceImageRepository;
    private final ImageStorage imageStorage;

    // 10) 이미지 업로드 (송금 건 생성)
    public RemittanceCreateResponse createRemittance(MultipartFile image) {
        String storedFileName = imageStorage.store(image);

        LocalDateTime now = LocalDateTime.now();
        Remittance remittance = Remittance.builder()
                .userId(TEMP_USER_ID)
                .status(RemittanceStatus.OCR_PROCESSING)
                .createdAt(now)
                .updatedAt(now)
                .build();
        Remittance savedRemittance = remittanceRepository.save(remittance);

        RemittanceImage remittanceImage = RemittanceImage.builder()
                .remittance(savedRemittance)
                .storedFileName(storedFileName)
                .createdAt(now)
                .build();
        RemittanceImage savedImage = remittanceImageRepository.save(remittanceImage);

        // TODO: AI팀 OCR 앙상블 서버 연동 시 이미지 전달 호출로 교체 (현재는 목업 - 실제 인식 없이 OCR_PROCESSING만 반환)

        return RemittanceCreateResponse.of(savedRemittance, savedImage);
    }

    // 11) OCR 결과 조회
    public OcrResultResponse getOcrResult(Long remittanceId) {
        Remittance remittance = remittanceRepository.findById(remittanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.REMITTANCE_NOT_FOUND));

        return OcrResultResponse.of(remittance);
    }

    // 12) 이미지 재요청 (재촬영)
    public RemittanceCreateResponse retryOcr(Long remittanceId, MultipartFile image) {
        Remittance remittance = remittanceRepository.findById(remittanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.REMITTANCE_NOT_FOUND));

        if (remittance.getStatus() == RemittanceStatus.COMPLETED) {
            throw new CustomException(ErrorCode.RETRY_NOT_ALLOWED);
        }

        String storedFileName = imageStorage.store(image);

        LocalDateTime now = LocalDateTime.now();
        remittance.retryOcr(now);

        RemittanceImage remittanceImage = RemittanceImage.builder()
                .remittance(remittance)
                .storedFileName(storedFileName)
                .createdAt(now)
                .build();
        RemittanceImage savedImage = remittanceImageRepository.save(remittanceImage);

        // TODO: AI팀 OCR 앙상블 서버 연동 시 재인식 이미지 전달 호출로 교체 (현재는 목업)

        return RemittanceCreateResponse.of(remittance, savedImage);
    }

    // 13) 인식결과 수정
    public RemittanceUpdateResponse updateRemittance(Long remittanceId, RemittanceUpdateRequest request) {
        Remittance remittance = remittanceRepository.findById(remittanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.REMITTANCE_NOT_FOUND));

        if (remittance.getStatus() == RemittanceStatus.COMPLETED) {
            throw new CustomException(ErrorCode.REMITTANCE_UPDATE_NOT_ALLOWED);
        }

        remittance.updateRecognizedFields(
                request.getRecipientName(),
                request.getBankName(),
                request.getAccountNumber(),
                request.getAmount());

        return RemittanceUpdateResponse.of(remittance);
    }

    // 5) 송금 확인
    public TransferResponse transfer (Long remittanceId) {
        // 1. Remittance 조회 -> 없으면 에러 처리
        Remittance remittance = remittanceRepository.findById (remittanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.REMITTANCE_NOT_FOUND));

        // 2. 이미 송금 완료 -> 에러 처리
        if (remittance.getStatus() == RemittanceStatus.COMPLETED) {
            throw new CustomException(ErrorCode.ALREADY_TRANSFERRED);
        }

        // 3. 계좌 번호나 송금 금액이 null -> 에러 처리
        if (remittance.getAccountNumber() == null || remittance.getAmount() == null) {
            throw new CustomException(ErrorCode.INVALID_TRANSFER_INFO);
        }

        // 4. 송금 완료로 변경
        remittance.complete();

        // 5. Transfer 엔티티 생성 . 저장
        Transfer transfer = Transfer.builder()
                .remittance(remittance)
                .recipientName(remittance.getRecipientName())
                .bankName(remittance.getBankName())
                .accountNumber(remittance.getAccountNumber())
                .amount(remittance.getAmount())
                .transferredAt(LocalDateTime.now())
                .build();

        Transfer saveTransfer = transferRepository.save(transfer);

        // 6. 응답 DTO로 변환해서 리턴
        return TransferResponse.of(saveTransfer);

    }

    // 6) 송금 결과 조회
    public TransferResponse getTransferResult (Long remittanceId) {

        // 1. 송금 내역 없으면 -> 에러 처리
        Transfer transfer = transferRepository.findByRemittanceId(remittanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSFER_NOT_FOUND));

        // 2. 있으면 결과 반환
        return TransferResponse.of(transfer);

    }
}
