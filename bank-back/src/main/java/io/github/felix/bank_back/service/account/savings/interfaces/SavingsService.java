package io.github.felix.bank_back.service.account.savings.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import io.github.felix.bank_back.model.account.Savings;


public interface SavingsService {
    List<Savings> findAll();

    Optional<Savings> findById(Long id);

    Savings save(Savings savings);

    void deleteById(Long id);

    BigDecimal getBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);

    List<Savings> findByPrimaryOwnerId(Long ownerId);

    List<Savings> findBySecondaryOwnerId(Long ownerId);

    void applyInterest(Long accountId);

    void checkMinimumBalance(Long accountId);
}
