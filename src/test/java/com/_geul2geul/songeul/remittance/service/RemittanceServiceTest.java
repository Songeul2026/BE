package com._geul2geul.songeul.remittance.service;

import com._geul2geul.songeul.common.exception.CustomException;
import com._geul2geul.songeul.common.storage.LocalImageStorage;
import com._geul2geul.songeul.remittance.domain.Remittance;
import com._geul2geul.songeul.remittance.domain.RemittanceImage;
import com._geul2geul.songeul.remittance.domain.RemittanceStatus;
import com._geul2geul.songeul.remittance.dto.RemittanceCreateResponse;
import com._geul2geul.songeul.remittance.repository.RemittanceImageRepository;
import com._geul2geul.songeul.remittance.repository.RemittanceRepository;
import com._geul2geul.songeul.remittance.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemittanceServiceTest {

    @Mock
    private RemittanceRepository remittanceRepository;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private RemittanceImageRepository remittanceImageRepository;
    @Mock
    private LocalImageStorage localImageStorage;

    @InjectMocks
    private RemittanceService remittanceService;

    private MultipartFile image;

    @BeforeEach
    void setUp() {
        image = new MockMultipartFile("image", "memo.jpg", "image/jpeg", "image-content".getBytes());
    }

    @Test
    void 이미지_업로드시_송금_건이_OCR_PROCESSING_상태로_생성된다() {
        when(localImageStorage.store(image)).thenReturn("stored-uuid.jpg");
        when(remittanceRepository.save(any(Remittance.class)))
                .thenAnswer(invocation -> {
                    Remittance remittance = invocation.getArgument(0);
                    return Remittance.builder()
                            .id(1L)
                            .userId(remittance.getUserId())
                            .status(remittance.getStatus())
                            .createdAt(remittance.getCreatedAt())
                            .updatedAt(remittance.getUpdatedAt())
                            .build();
                });
        when(remittanceImageRepository.save(any(RemittanceImage.class)))
                .thenAnswer(invocation -> {
                    RemittanceImage remittanceImage = invocation.getArgument(0);
                    return RemittanceImage.builder()
                            .id(10L)
                            .remittance(remittanceImage.getRemittance())
                            .storedFileName(remittanceImage.getStoredFileName())
                            .createdAt(remittanceImage.getCreatedAt())
                            .build();
                });

        RemittanceCreateResponse response = remittanceService.createRemittance(image);

        assertThat(response.getRemittanceId()).isEqualTo(1L);
        assertThat(response.getImageId()).isEqualTo(10L);
        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.OCR_PROCESSING);
    }

    @Test
    void 이미지_검증에_실패하면_송금_건을_생성하지_않는다() {
        when(localImageStorage.store(image)).thenThrow(new CustomException(
                com._geul2geul.songeul.common.exception.ErrorCode.INVALID_IMAGE_FILE));

        assertThrows(CustomException.class, () -> remittanceService.createRemittance(image));
    }

    @Test
    void 재촬영_요청시_상태가_OCR_PROCESSING으로_변경되고_새_이미지가_저장된다() {
        Remittance remittance = Remittance.builder()
                .id(1L)
                .status(RemittanceStatus.OCR_FAILED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(remittanceRepository.findById(1L)).thenReturn(Optional.of(remittance));
        when(localImageStorage.store(image)).thenReturn("retake-uuid.jpg");
        when(remittanceImageRepository.save(any(RemittanceImage.class)))
                .thenAnswer(invocation -> {
                    RemittanceImage remittanceImage = invocation.getArgument(0);
                    return RemittanceImage.builder()
                            .id(20L)
                            .remittance(remittanceImage.getRemittance())
                            .storedFileName(remittanceImage.getStoredFileName())
                            .createdAt(remittanceImage.getCreatedAt())
                            .build();
                });

        RemittanceCreateResponse response = remittanceService.retryOcr(1L, image);

        assertThat(response.getRemittanceId()).isEqualTo(1L);
        assertThat(response.getImageId()).isEqualTo(20L);
        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.OCR_PROCESSING);
    }

    @Test
    void 이미_송금_완료된_건은_재촬영_요청시_예외가_발생한다() {
        Remittance remittance = Remittance.builder()
                .id(1L)
                .status(RemittanceStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(remittanceRepository.findById(1L)).thenReturn(Optional.of(remittance));

        assertThrows(CustomException.class, () -> remittanceService.retryOcr(1L, image));

        verify(localImageStorage, never()).store(any());
    }

    @Test
    void 존재하지_않는_송금_건은_재촬영_요청시_예외가_발생한다() {
        when(remittanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> remittanceService.retryOcr(999L, image));
    }

}
