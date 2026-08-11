package com._geul2geul.songeul.remittance.service;

import com._geul2geul.songeul.common.exception.CustomException;
import com._geul2geul.songeul.common.storage.ImageStorage;
import com._geul2geul.songeul.remittance.client.OcrClient;
import com._geul2geul.songeul.remittance.client.OcrFieldResult;
import com._geul2geul.songeul.remittance.client.OcrResponse;
import com._geul2geul.songeul.remittance.domain.OcrFieldMeta;
import com._geul2geul.songeul.remittance.domain.Remittance;
import com._geul2geul.songeul.remittance.domain.RemittanceImage;
import com._geul2geul.songeul.remittance.domain.RemittanceStatus;
import com._geul2geul.songeul.remittance.dto.OcrResultResponse;
import com._geul2geul.songeul.remittance.dto.RemittanceCreateResponse;
import com._geul2geul.songeul.remittance.dto.RemittanceUpdateRequest;
import com._geul2geul.songeul.remittance.dto.RemittanceUpdateResponse;
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
import org.springframework.web.client.ResourceAccessException;
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
    private ImageStorage imageStorage;
    @Mock
    private OcrClient ocrClient;

    @InjectMocks
    private RemittanceService remittanceService;

    private MultipartFile image;

    @BeforeEach
    void setUp() {
        image = new MockMultipartFile("image", "memo.jpg", "image/jpeg", "image-content".getBytes());
    }

    private OcrResponse ocrSuccessResponse() {
        return OcrResponse.builder()
                .name(OcrFieldResult.builder().value("정대호").confidence(0.99).needsConfirmation(false).build())
                .bank(OcrFieldResult.builder().value("하나").confidence(0.99).needsConfirmation(false).build())
                .account(OcrFieldResult.builder().value("45678901234").confidence(0.99).needsConfirmation(true).build())
                .amount(OcrFieldResult.builder().value(500000).confidence(0.99).needsConfirmation(false).build())
                .build();
    }

    private void stubRemittanceAndImageSave() {
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
    }

    @Test
    void 이미지_업로드시_OCR이_성공하면_OCR_COMPLETED_상태로_필드가_채워진다() {
        when(imageStorage.store(image)).thenReturn("stored-uuid.jpg");
        when(ocrClient.requestOcr(image)).thenReturn(ocrSuccessResponse());
        stubRemittanceAndImageSave();

        RemittanceCreateResponse response = remittanceService.createRemittance(image);

        assertThat(response.getRemittanceId()).isEqualTo(1L);
        assertThat(response.getImageId()).isEqualTo(10L);
        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.OCR_COMPLETED);
    }

    @Test
    void 이미지_업로드시_OCR_호출이_실패하면_OCR_FAILED_상태가_된다() {
        when(imageStorage.store(image)).thenReturn("stored-uuid.jpg");
        when(ocrClient.requestOcr(image)).thenThrow(new ResourceAccessException("연결 실패"));
        stubRemittanceAndImageSave();

        RemittanceCreateResponse response = remittanceService.createRemittance(image);

        assertThat(response.getRemittanceId()).isEqualTo(1L);
        assertThat(response.getImageId()).isEqualTo(10L);
        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.OCR_FAILED);
    }

    @Test
    void 이미지_검증에_실패하면_송금_건을_생성하지_않는다() {
        when(imageStorage.store(image)).thenThrow(new CustomException(
                com._geul2geul.songeul.common.exception.ErrorCode.INVALID_IMAGE_FILE));

        assertThrows(CustomException.class, () -> remittanceService.createRemittance(image));
    }

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

    @Test
    void 재촬영_요청시_OCR이_성공하면_OCR_COMPLETED_상태로_변경되고_새_이미지가_저장된다() {
        Remittance remittance = Remittance.builder()
                .id(1L)
                .status(RemittanceStatus.OCR_FAILED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(remittanceRepository.findById(1L)).thenReturn(Optional.of(remittance));
        when(imageStorage.store(image)).thenReturn("retake-uuid.jpg");
        when(ocrClient.requestOcr(image)).thenReturn(ocrSuccessResponse());
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
        assertThat(response.getStatus()).isEqualTo(RemittanceStatus.OCR_COMPLETED);
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

        verify(imageStorage, never()).store(any());
    }

    @Test
    void 존재하지_않는_송금_건은_재촬영_요청시_예외가_발생한다() {
        when(remittanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> remittanceService.retryOcr(999L, image));
    }

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
