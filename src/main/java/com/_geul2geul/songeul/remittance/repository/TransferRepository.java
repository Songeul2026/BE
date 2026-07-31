package com._geul2geul.songeul.remittance.repository;

import com._geul2geul.songeul.remittance.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository <Transfer,Long> {
}
