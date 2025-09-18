package io.github.felix.bank_back.service.account.savings.impl;

import io.github.felix.bank_back.dto.account.savings.SavingsCreateDTO;
import io.github.felix.bank_back.dto.account.savings.SavingsResponseDTO;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.model.account.Savings;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.repository.account.SavingsRepository;
import io.github.felix.bank_back.repository.user.AccountHolderRepository;
import io.github.felix.bank_back.service.account.savings.interfaces.SavingsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
public class SavingsServiceImpl implements SavingsService {

    private final SavingsRepository savingsRepository;
    private final AccountHolderRepository accountHolderRepository;

    public SavingsServiceImpl(SavingsRepository savingsRepository,
                              AccountHolderRepository accountHolderRepository) {
        this.savingsRepository = savingsRepository;
        this.accountHolderRepository = accountHolderRepository;
    }

    @Override
    public List<SavingsResponseDTO> findAll() {
        return savingsRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public Optional<SavingsResponseDTO> findById(Long id) {
        return savingsRepository.findById(id).map(this::toDTO);
    }

    @Override
    public SavingsResponseDTO create(SavingsCreateDTO dto) {
        AccountHolder primary = accountHolderRepository.findById(dto.getPrimaryOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Primary owner no encontrado"));
        AccountHolder secondary = null;
        if (dto.getSecondaryOwnerId() != null) {
            secondary = accountHolderRepository.findById(dto.getSecondaryOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Secondary owner no encontrado"));
        }
        Currency currency = Currency.getInstance(dto.getCurrencyCode());
        Savings sv;
        if (dto.getInterestRate() != null) {
            sv = (secondary == null)
                    ? new Savings(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary, dto.getInterestRate())
                    : new Savings(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary, secondary, dto.getInterestRate());
        } else {
            sv = (secondary == null)
                    ? new Savings(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary)
                    : new Savings(new Money(dto.getBalanceAmount(), currency), dto.getSecretKey(), primary, secondary);
        }
        if (dto.getMinimumBalance() != null) {
            sv.setMinimumBalance(new Money(dto.getMinimumBalance(), currency));
        }
        Savings saved = savingsRepository.save(sv);
        return toDTO(saved);
    }

    @Override
    public void deleteById(Long id) {
        savingsRepository.deleteById(id);
    }

    @Override
    public BigDecimal getBalance(Long accountId) {
        Savings acc = savingsRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Savings no encontrada"));
        return acc.getBalance().getAmount();
    }

    @Override
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        Savings acc = savingsRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Savings no encontrada"));
        Currency currency = acc.getBalance().getCurrencyCode();
        acc.setBalance(new Money(newBalance, currency));
        savingsRepository.save(acc);
    }

    @Override
    public List<SavingsResponseDTO> findByPrimaryOwnerId(Long ownerId) {
        return savingsRepository.findByPrimaryOwner_Id(ownerId).stream().map(this::toDTO).toList();
    }

    @Override
    public List<SavingsResponseDTO> findBySecondaryOwnerId(Long ownerId) {
        return savingsRepository.findBySecondaryOwner_Id(ownerId).stream().map(this::toDTO).toList();
    }

    @Override
    public SavingsResponseDTO applyInterest(Long accountId) {
        Savings acc = savingsRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Savings no encontrada"));
        if (acc.applyAnnualInterest()) {
            acc = savingsRepository.save(acc);
        }
        return toDTO(acc);
    }

    @Override
    public SavingsResponseDTO checkMinimumBalance(Long accountId) {
        Savings acc = savingsRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Savings no encontrada"));
        if (acc.isBelowMinimumBalance()) {
            // Penalización está en Account; asumimos que otra capa la aplica si hace falta.
        }
        return toDTO(acc);
    }

    // ================= MAPPER PRIVADO =================
    private SavingsResponseDTO toDTO(Savings e) {
        SavingsResponseDTO dto = new SavingsResponseDTO();
        dto.setId(e.getId());
        dto.setBalance(e.getBalance().getAmount());
        dto.setCreationDate(e.getCreationDate());
        dto.setStatus(e.getStatus());
        dto.setAccountType(e.getAccountType());
        dto.setPenaltyFee(e.getPenaltyFee().getAmount());
        dto.setPrimaryOwner(AccountHolderDTO.fromEntity(e.getPrimaryOwner()));
        dto.setSecondaryOwner(AccountHolderDTO.fromEntity(e.getSecondaryOwner()));
        dto.setMinimumBalance(e.getMinimumBalance().getAmount());
        dto.setInterestRate(e.getInterestRate());
        return dto;
    }
}
