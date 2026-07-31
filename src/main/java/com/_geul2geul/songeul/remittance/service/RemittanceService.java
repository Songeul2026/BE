package com._geul2geul.songeul.remittance.service;

import com._geul2geul.songeul.common.exception.CustomException;
import com._geul2geul.songeul.common.exception.ErrorCode;
import com._geul2geul.songeul.remittance.domain.Remittance;
import com._geul2geul.songeul.remittance.domain.RemittanceStatus;
import com._geul2geul.songeul.remittance.domain.Transfer;
import com._geul2geul.songeul.remittance.dto.TransferResponse;
import com._geul2geul.songeul.remittance.repository.RemittanceRepository;
import com._geul2geul.songeul.remittance.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RemittanceService {

    private final RemittanceRepository remittanceRepository;
    private final TransferRepository transferRepository;

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
}
