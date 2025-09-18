package io.github.felix.bank_back.service.account.savings.interfaces;

import io.github.felix.bank_back.dto.account.savings.SavingsCreateDTO;
import io.github.felix.bank_back.dto.account.savings.SavingsResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface SavingsService {
    List<SavingsResponseDTO> findAll();

    Optional<SavingsResponseDTO> findById(Long id);

    SavingsResponseDTO create(SavingsCreateDTO dto);

    void deleteById(Long id);

    BigDecimal getBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);

    List<SavingsResponseDTO> findByPrimaryOwnerId(Long ownerId);

    List<SavingsResponseDTO> findBySecondaryOwnerId(Long ownerId);

    SavingsResponseDTO applyInterest(Long accountId);

    SavingsResponseDTO checkMinimumBalance(Long accountId);
}
