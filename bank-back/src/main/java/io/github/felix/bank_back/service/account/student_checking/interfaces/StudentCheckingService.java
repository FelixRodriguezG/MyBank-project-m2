package io.github.felix.bank_back.service.account.student_checking.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import io.github.felix.bank_back.model.account.StudentChecking;

public interface StudentCheckingService {
    List<StudentChecking> findAll();

    Optional<StudentChecking> findById(Long id);

    StudentChecking save(StudentChecking studentChecking);

    void deleteById(Long id);

    BigDecimal getBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);

    List<StudentChecking> findByPrimaryOwnerId(Long ownerId);

    List<StudentChecking> findBySecondaryOwnerId(Long ownerId);
}
