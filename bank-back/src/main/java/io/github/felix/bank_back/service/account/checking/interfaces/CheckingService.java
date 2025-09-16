package io.github.felix.bank_back.service.account.checking.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import io.github.felix.bank_back.model.account.Checking;

public interface CheckingService {
    List<Checking> findAll();

    Optional<Checking> findById(Long id);

    Checking save(Checking checking);

    void deleteById(Long id);

    BigDecimal getBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);

    List<Checking> findByPrimaryOwnerId(Long ownerId);

    List<Checking> findBySecondaryOwnerId(Long ownerId);

    void applyMonthlyMaintenanceFee(Long accountId);

    void checkMinimumBalance(Long accountId);
}
