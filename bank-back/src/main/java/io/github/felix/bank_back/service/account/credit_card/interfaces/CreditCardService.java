package io.github.felix.bank_back.service.account.credit_card.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import io.github.felix.bank_back.model.account.CreditCard;

public interface CreditCardService {
    List<CreditCard> findAll();

    Optional<CreditCard> findById(Long id);

    CreditCard save(CreditCard creditCard);

    void deleteById(Long id);

    BigDecimal getBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);

    List<CreditCard> findByPrimaryOwnerId(Long ownerId);

    List<CreditCard> findBySecondaryOwnerId(Long ownerId);

    void applyInterest(Long accountId);

    void checkCreditLimit(Long accountId, BigDecimal amount);
}
