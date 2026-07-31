package com._geul2geul.songeul.remittance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name="transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remittance_id")
    private Remittance remittance;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    private Long amount;

    @Column(name = "transferred_at", nullable = false)
    private LocalDateTime transferredAt;

}
