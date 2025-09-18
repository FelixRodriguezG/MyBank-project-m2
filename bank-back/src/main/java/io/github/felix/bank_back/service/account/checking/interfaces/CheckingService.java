package io.github.felix.bank_back.service.account.checking.interfaces;

import io.github.felix.bank_back.dto.account.checking.CheckingCreateDTO;
import io.github.felix.bank_back.dto.account.checking.CheckingResponseDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CheckingService {
    List<CheckingResponseDTO> findAll();

    Optional<CheckingResponseDTO> findById(Long id);

    CheckingResponseDTO create(CheckingCreateDTO dto);

    void deleteById(Long id);

    BigDecimal getBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);

    List<CheckingResponseDTO> findByPrimaryOwnerId(Long ownerId);

    List<CheckingResponseDTO> findBySecondaryOwnerId(Long ownerId);

    CheckingResponseDTO applyMonthlyMaintenanceFee(Long accountId);

    CheckingResponseDTO checkMinimumBalance(Long accountId);
}
