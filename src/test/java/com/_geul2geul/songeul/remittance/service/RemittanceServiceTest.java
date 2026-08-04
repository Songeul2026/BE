package com._geul2geul.songeul.remittance.service;

import com._geul2geul.songeul.common.exception.CustomException;
import com._geul2geul.songeul.remittance.domain.OcrFieldMeta;
import com._geul2geul.songeul.remittance.domain.Remittance;
import com._geul2geul.songeul.remittance.domain.RemittanceStatus;
import com._geul2geul.songeul.remittance.dto.OcrResultResponse;
import com._geul2geul.songeul.remittance.repository.RemittanceRepository;
import com._geul2geul.songeul.remittance.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemittanceServiceTest {

    @Mock
    private RemittanceRepository remittanceRepository;
    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private RemittanceService remittanceService;

    @Test
    void OCR_처리중이면_필드_없이_상태만_반환한다() {
        Remittance remittance = Remittance.builder()
                .id(1L)
                .status(RemittanceStatus.OCR_PROCESSING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(remittanceRepository.findById(1L)).thenReturn(Optional.of(remittance));

        OcrResultResponse response = remittanceService.getOcrResult(1L);

        assertThat(response.getRemittanceId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.OCR_PROCESSING);
        assertThat(response.getFields()).isNull();
    }

    @Test
    void OCR_완료면_인식된_필드를_함께_반환한다() {
        Remittance remittance = Remittance.builder()
                .id(1L)
                .status(RemittanceStatus.OCR_COMPLETED)
                .recipientName("박영수")
                .bankName("국민은행")
                .accountNumber("123456789012")
                .amount(50000L)
                .nameMeta(new OcrFieldMeta(0.92, false))
                .bankMeta(new OcrFieldMeta(0.88, false))
                .accountMeta(new OcrFieldMeta(0.65, true))
                .amountMeta(new OcrFieldMeta(0.78, false))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(remittanceRepository.findById(1L)).thenReturn(Optional.of(remittance));

        OcrResultResponse response = remittanceService.getOcrResult(1L);

        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.OCR_COMPLETED);
        assertThat(response.getFields()).isNotNull();
        assertThat(response.getFields().getName().getValue()).isEqualTo("박영수");
        assertThat(response.getFields().getAccount().isNeedsConfirmation()).isTrue();
        assertThat(response.getFields().getAmount().getValue()).isEqualTo(50000L);
    }

    @Test
    void OCR_실패면_필드_없이_상태만_반환한다() {
        Remittance remittance = Remittance.builder()
                .id(1L)
                .status(RemittanceStatus.OCR_FAILED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(remittanceRepository.findById(1L)).thenReturn(Optional.of(remittance));

        OcrResultResponse response = remittanceService.getOcrResult(1L);

        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.OCR_FAILED);
        assertThat(response.getFields()).isNull();
    }

    @Test
    void 존재하지_않는_송금_건이면_예외가_발생한다() {
        when(remittanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> remittanceService.getOcrResult(999L));
    }

}
