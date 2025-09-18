package io.github.felix.bank_back.service.account.credit_card.interfaces;

import io.github.felix.bank_back.dto.account.credit_card.CreditCardCreateDTO;
import io.github.felix.bank_back.dto.account.credit_card.CreditCardResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface CreditCardService {

    List<CreditCardResponseDTO> findAll();

    Optional<CreditCardResponseDTO> findById(Long id);

    CreditCardResponseDTO create(CreditCardCreateDTO dto);

    void deleteById(Long id);

    BigDecimal getBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);

    List<CreditCardResponseDTO> findByPrimaryOwnerId(Long ownerId);

    List<CreditCardResponseDTO> findBySecondaryOwnerId(Long ownerId);

    CreditCardResponseDTO applyInterest(Long accountId);

    void checkCreditLimit(Long accountId, BigDecimal amount);
}
