package com._geul2geul.songeul.remittance.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "remittances")
public class Remittance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private RemittanceStatus status;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column
    private Long amount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "confidence", column = @Column(name = "name_confidence")),
            @AttributeOverride(name = "needsConfirmation", column = @Column(name = "name_needs_confirmation"))
    })
    private OcrFieldMeta nameMeta;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "confidence", column = @Column(name = "bank_confidence")),
            @AttributeOverride(name = "needsConfirmation", column = @Column(name = "bank_needs_confirmation"))
    })
    private OcrFieldMeta bankMeta;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "confidence", column = @Column(name = "account_confidence")),
            @AttributeOverride(name = "needsConfirmation", column = @Column(name = "account_needs_confirmation"))
    })
    private OcrFieldMeta accountMeta;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "confidence", column = @Column(name = "amount_confidence")),
            @AttributeOverride(name = "needsConfirmation", column = @Column(name = "amount_needs_confirmation"))
    })
    private OcrFieldMeta amountMeta;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void complete() {
        this.status = RemittanceStatus.COMPLETED;
    }

    public void updateRecognizedFields(String recipientName, String bankName, String accountNumber, Long amount) {
        if (recipientName != null) {
            this.recipientName = recipientName;
        }
        if (bankName != null) {
            this.bankName = bankName;
        }
        if (accountNumber != null) {
            this.accountNumber = accountNumber;
        }
        if (amount != null) {
            this.amount = amount;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void retryOcr(LocalDateTime updatedAt) {
        this.status = RemittanceStatus.OCR_PROCESSING;
        this.updatedAt = updatedAt;
    }

    public void applyOcrResult(String recipientName, OcrFieldMeta nameMeta,
                                String bankName, OcrFieldMeta bankMeta,
                                String accountNumber, OcrFieldMeta accountMeta,
                                Long amount, OcrFieldMeta amountMeta,
                                LocalDateTime updatedAt) {
        this.recipientName = recipientName;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.nameMeta = nameMeta;
        this.bankMeta = bankMeta;
        this.accountMeta = accountMeta;
        this.amountMeta = amountMeta;
        this.status = RemittanceStatus.OCR_COMPLETED;
        this.updatedAt = updatedAt;
    }

    public void failOcr(LocalDateTime updatedAt) {
        this.status = RemittanceStatus.OCR_FAILED;
        this.updatedAt = updatedAt;
    }

}
