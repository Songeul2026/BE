package com._geul2geul.songeul.remittance.repository;

import com._geul2geul.songeul.remittance.domain.Remittance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemittanceRepository extends JpaRepository <Remittance, Long> {
}
