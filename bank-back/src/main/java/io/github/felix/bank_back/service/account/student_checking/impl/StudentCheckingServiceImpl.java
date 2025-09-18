package io.github.felix.bank_back.service.account.student_checking.impl;

import io.github.felix.bank_back.dto.account.student_checking.StudentCheckingCreateDTO;
import io.github.felix.bank_back.dto.account.student_checking.StudentCheckingResponseDTO;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.model.account.StudentChecking;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.repository.account.StudentCheckingRepository;
import io.github.felix.bank_back.repository.user.AccountHolderRepository;
import io.github.felix.bank_back.service.account.student_checking.interfaces.StudentCheckingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
public class StudentCheckingServiceImpl implements StudentCheckingService {

    private final StudentCheckingRepository studentCheckingRepository;
    private final AccountHolderRepository accountHolderRepository;

    public StudentCheckingServiceImpl(StudentCheckingRepository studentCheckingRepository,
                                      AccountHolderRepository accountHolderRepository) {
        this.studentCheckingRepository = studentCheckingRepository;
        this.accountHolderRepository = accountHolderRepository;
    }

    @Override
    public List<StudentCheckingResponseDTO> findAll() {
        return studentCheckingRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public Optional<StudentCheckingResponseDTO> findById(Long id) {
        return studentCheckingRepository.findById(id).map(this::toDTO);
    }

    @Override
    public StudentCheckingResponseDTO create(StudentCheckingCreateDTO dto) {
        AccountHolder primary = accountHolderRepository.findById(dto.getPrimaryOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Primary owner no encontrado"));
        AccountHolder secondary = null;
        if (dto.getSecondaryOwnerId() != null) {
            secondary = accountHolderRepository.findById(dto.getSecondaryOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Secondary owner no encontrado"));
        }
        Currency currency = Currency.getInstance(dto.getCurrencyCode());
        StudentChecking sc = (secondary == null)
                ? new StudentChecking(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary)
                : new StudentChecking(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary, secondary);
        StudentChecking saved = studentCheckingRepository.save(sc);
        return toDTO(saved);
    }

    @Override
    public void deleteById(Long id) {
        studentCheckingRepository.deleteById(id);
    }

    @Override
    public BigDecimal getBalance(Long accountId) {
        StudentChecking acc = studentCheckingRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("StudentChecking no encontrado"));
        return acc.getBalance().getAmount();
    }

    @Override
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        StudentChecking acc = studentCheckingRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("StudentChecking no encontrado"));
        Currency currency = acc.getBalance().getCurrencyCode();
        acc.setBalance(new Money(newBalance, currency));
        studentCheckingRepository.save(acc);
    }

    @Override
    public List<StudentCheckingResponseDTO> findByPrimaryOwnerId(Long ownerId) {
        return studentCheckingRepository.findByPrimaryOwner_Id(ownerId).stream().map(this::toDTO).toList();
    }

    @Override
    public List<StudentCheckingResponseDTO> findBySecondaryOwnerId(Long ownerId) {
        return studentCheckingRepository.findBySecondaryOwner_Id(ownerId).stream().map(this::toDTO).toList();
    }

    // =============== MAPPER PRIVADO ===============
    private StudentCheckingResponseDTO toDTO(StudentChecking e) {
        StudentCheckingResponseDTO dto = new StudentCheckingResponseDTO();
        dto.setId(e.getId());
        dto.setBalance(e.getBalance().getAmount());
        dto.setCreationDate(e.getCreationDate());
        dto.setStatus(e.getStatus());
        dto.setAccountType(e.getAccountType());
        dto.setPenaltyFee(e.getPenaltyFee().getAmount());
        dto.setPrimaryOwner(AccountHolderDTO.fromEntity(e.getPrimaryOwner()));
        dto.setSecondaryOwner(AccountHolderDTO.fromEntity(e.getSecondaryOwner()));
        return dto;
    }
}
