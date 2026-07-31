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

}
