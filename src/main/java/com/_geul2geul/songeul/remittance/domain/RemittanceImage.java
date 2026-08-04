package com._geul2geul.songeul.remittance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "remittance_images")
public class RemittanceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remittance_id", nullable = false)
    private Remittance remittance;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
