package com._geul2geul.songeul.remittance.repository;

import com._geul2geul.songeul.remittance.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferRepository extends JpaRepository <Transfer,Long> {

    Optional<Transfer> findByRemittanceId(Long remittanceId);

}
