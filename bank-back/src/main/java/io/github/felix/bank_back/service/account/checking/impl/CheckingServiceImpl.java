package io.github.felix.bank_back.service.account.checking.impl;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.dto.account.checking.CheckingCreateDTO;
import io.github.felix.bank_back.dto.account.checking.CheckingResponseDTO;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.model.account.Checking;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.repository.account.CheckingRepository;
import io.github.felix.bank_back.repository.user.AccountHolderRepository;
import io.github.felix.bank_back.service.account.checking.interfaces.CheckingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
public class CheckingServiceImpl implements CheckingService {

    private final CheckingRepository checkingRepository;
    private final AccountHolderRepository accountHolderRepository;

    public CheckingServiceImpl(CheckingRepository checkingRepository,
                               AccountHolderRepository accountHolderRepository) {
        this.checkingRepository = checkingRepository;
        this.accountHolderRepository = accountHolderRepository;
    }

    @Override
    public List<CheckingResponseDTO> findAll() {
        return checkingRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public Optional<CheckingResponseDTO> findById(Long id) {
        return checkingRepository.findById(id).map(this::toDTO);
    }

    @Override
    public CheckingResponseDTO create(CheckingCreateDTO dto) {
        AccountHolder primary = accountHolderRepository.findById(dto.getPrimaryOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Primary owner no encontrado"));
        AccountHolder secondary = null;
        if (dto.getSecondaryOwnerId() != null) {
            secondary = accountHolderRepository.findById(dto.getSecondaryOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Secondary owner no encontrado"));
        }
        Currency currency = Currency.getInstance(dto.getCurrencyCode());
        Checking checking = (secondary == null)
                ? new Checking(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary)
                : new Checking(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary, secondary);
        Checking saved = checkingRepository.save(checking);
        return toDTO(saved);
    }

    @Override
    public void deleteById(Long id) {
        checkingRepository.deleteById(id);
    }

    @Override
    public BigDecimal getBalance(Long accountId) {
        Checking acc = checkingRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Checking no encontrado"));
        return acc.getBalance().getAmount();
    }

    @Override
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        Checking acc = checkingRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Checking no encontrado"));
        Currency currency = acc.getBalance().getCurrencyCode();
        acc.setBalance(new Money(newBalance, currency));
        checkingRepository.save(acc);
    }

    @Override
    public List<CheckingResponseDTO> findByPrimaryOwnerId(Long ownerId) {
        return checkingRepository.findByPrimaryOwner_Id(ownerId).stream().map(this::toDTO).toList();
    }

    @Override
    public List<CheckingResponseDTO> findBySecondaryOwnerId(Long ownerId) {
        return checkingRepository.findBySecondaryOwner_Id(ownerId).stream().map(this::toDTO).toList();
    }

    @Override
    public CheckingResponseDTO applyMonthlyMaintenanceFee(Long accountId) {
        Checking acc = checkingRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Checking no encontrado"));
        acc.applyMonthlyMaintenanceFee();
        Checking saved = checkingRepository.save(acc);
        return toDTO(saved);
    }

    @Override
    public CheckingResponseDTO checkMinimumBalance(Long accountId) {
        Checking acc = checkingRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Checking no encontrado"));
        acc.applyPenaltyIfBelowMinimum();
        Checking saved = checkingRepository.save(acc);
        return toDTO(saved);
    }

    // ================= MAPPERS PRIVADOS =================
    private CheckingResponseDTO toDTO(Checking e) {
        CheckingResponseDTO dto = new CheckingResponseDTO();
        // campos comunes (heredados)
        dto.setId(e.getId());
        dto.setBalance(e.getBalance().getAmount());
        dto.setCreationDate(e.getCreationDate());
        dto.setStatus(e.getStatus());
        dto.setAccountType(e.getAccountType());
        dto.setPenaltyFee(e.getPenaltyFee().getAmount());
        dto.setPrimaryOwner(AccountHolderDTO.fromEntity(e.getPrimaryOwner()));
        dto.setSecondaryOwner(AccountHolderDTO.fromEntity(e.getSecondaryOwner()));
        // específicos
        dto.setMinimumBalance(e.getMinimumBalance().getAmount());
        return dto;
    }
}
