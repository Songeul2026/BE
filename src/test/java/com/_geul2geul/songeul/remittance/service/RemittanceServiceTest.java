package com._geul2geul.songeul.remittance.service;

import com._geul2geul.songeul.common.exception.CustomException;
import com._geul2geul.songeul.remittance.domain.Remittance;
import com._geul2geul.songeul.remittance.domain.RemittanceStatus;
import com._geul2geul.songeul.remittance.dto.RemittanceUpdateRequest;
import com._geul2geul.songeul.remittance.dto.RemittanceUpdateResponse;
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
    void 전달된_필드만_부분_수정된다() {
        Remittance remittance = Remittance.builder()
                .id(1L)
                .status(RemittanceStatus.OCR_COMPLETED)
                .recipientName("박영수")
                .bankName("국민은행")
                .accountNumber("123-456-789012")
                .amount(50000L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(remittanceRepository.findById(1L)).thenReturn(Optional.of(remittance));

        RemittanceUpdateRequest request = RemittanceUpdateRequest.builder()
                .amount(70000L)
                .build();

        RemittanceUpdateResponse response = remittanceService.updateRemittance(1L, request);

        assertThat(response.getRemittanceId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualTo(70000L);
        assertThat(response.getRecipientName()).isEqualTo("박영수");
        assertThat(response.getBankName()).isEqualTo("국민은행");
        assertThat(response.getAccountNumber()).isEqualTo("123-456-789012");
    }

    @Test
    void 이미_송금_완료된_건은_수정시_예외가_발생한다() {
        Remittance remittance = Remittance.builder()
                .id(1L)
                .status(RemittanceStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(remittanceRepository.findById(1L)).thenReturn(Optional.of(remittance));

        RemittanceUpdateRequest request = RemittanceUpdateRequest.builder()
                .recipientName("박영수")
                .build();

        assertThrows(CustomException.class, () -> remittanceService.updateRemittance(1L, request));
    }

    @Test
    void 존재하지_않는_송금_건은_수정시_예외가_발생한다() {
        when(remittanceRepository.findById(999L)).thenReturn(Optional.empty());

        RemittanceUpdateRequest request = RemittanceUpdateRequest.builder()
                .recipientName("박영수")
                .build();

        assertThrows(CustomException.class, () -> remittanceService.updateRemittance(999L, request));
    }

}
