package io.github.felix.bank_back.service.account.savings.impl;

import io.github.felix.bank_back.dto.account.savings.SavingsCreateDTO;
import io.github.felix.bank_back.dto.account.savings.SavingsResponseDTO;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.model.account.Savings;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.transaction.Transaction;
import io.github.felix.bank_back.model.transaction.enums.TransactionType;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.repository.account.SavingsRepository;
import io.github.felix.bank_back.repository.transaction.TransactionRepository;
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
    private final TransactionRepository transactionRepository;

    public SavingsServiceImpl(SavingsRepository savingsRepository,
                              AccountHolderRepository accountHolderRepository,
                              TransactionRepository transactionRepository) {
        this.savingsRepository = savingsRepository;
        this.accountHolderRepository = accountHolderRepository;
        this.transactionRepository = transactionRepository;
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
        BigDecimal old = acc.getBalance().getAmount();
        acc.setBalance(new Money(newBalance, currency));
        savingsRepository.save(acc);
        // Registrar transacción por ajuste de balance (depósito o retiro)
        BigDecimal diff = newBalance.subtract(old);
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            transactionRepository.save(new Transaction(new Money(diff, currency), TransactionType.DEPOSIT, acc, "Balance update"));
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            transactionRepository.save(new Transaction(new Money(diff.abs(), currency), TransactionType.WITHDRAWAL, acc, "Balance update"));
        }
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
        // Calcular interés antes de aplicarlo para registrarlo
        Money interest = acc.calculateAnnualInterest();
        boolean applied = acc.applyAnnualInterest();
        if (applied && interest.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            acc = savingsRepository.save(acc);
            transactionRepository.save(new Transaction(new Money(interest.getAmount(), interest.getCurrencyCode()),
                    TransactionType.INTEREST_PAYMENT, acc, "Savings annual interest"));
        }
        return toDTO(acc);
    }

    @Override
    public SavingsResponseDTO checkMinimumBalance(Long accountId) {
        Savings acc = savingsRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Savings no encontrada"));
        if (acc.isBelowMinimumBalance()) {
            Money penalty = new Money(BigDecimal.valueOf(40), acc.getBalance().getCurrencyCode());
            acc.setBalance(new Money(acc.getBalance().decreaseAmount(penalty)));
            acc = savingsRepository.save(acc);
            transactionRepository.save(new Transaction(new Money(penalty.getAmount(), penalty.getCurrencyCode()),
                    TransactionType.PENALTY_FEE, acc, "Penalty fee below minimum (savings)"));
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
