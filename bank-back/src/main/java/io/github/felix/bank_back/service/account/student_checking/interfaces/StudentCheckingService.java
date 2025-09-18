package io.github.felix.bank_back.service.account.student_checking.interfaces;

import io.github.felix.bank_back.dto.account.student_checking.StudentCheckingCreateDTO;
import io.github.felix.bank_back.dto.account.student_checking.StudentCheckingResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StudentCheckingService {
    List<StudentCheckingResponseDTO> findAll();

    Optional<StudentCheckingResponseDTO> findById(Long id);

    StudentCheckingResponseDTO create(StudentCheckingCreateDTO dto);

    void deleteById(Long id);

    BigDecimal getBalance(Long accountId);

    void updateBalance(Long accountId, BigDecimal newBalance);

    List<StudentCheckingResponseDTO> findByPrimaryOwnerId(Long ownerId);

    List<StudentCheckingResponseDTO> findBySecondaryOwnerId(Long ownerId);
}
