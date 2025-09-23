package io.github.felix.bank_back.service.account.checking.impl;

import io.github.felix.bank_back.dto.account.AccountResponseDTO;
import io.github.felix.bank_back.dto.account.checking.CheckingCreateDTO;
import io.github.felix.bank_back.dto.account.checking.CheckingResponseDTO;
import io.github.felix.bank_back.dto.user.account_holder.AccountHolderDTO;
import io.github.felix.bank_back.model.account.Checking;
import io.github.felix.bank_back.model.account.embedded.Money;
import io.github.felix.bank_back.model.transaction.Transaction;
import io.github.felix.bank_back.model.transaction.enums.TransactionType;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.repository.account.CheckingRepository;
import io.github.felix.bank_back.repository.transaction.TransactionRepository;
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
    private final TransactionRepository transactionRepository;

    public CheckingServiceImpl(CheckingRepository checkingRepository,
                               AccountHolderRepository accountHolderRepository,
                               TransactionRepository transactionRepository) {
        this.checkingRepository = checkingRepository;
        this.accountHolderRepository = accountHolderRepository;
        this.transactionRepository = transactionRepository;
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
        BigDecimal old = acc.getBalance().getAmount();
        acc.setBalance(new Money(newBalance, currency));
        checkingRepository.save(acc);
        // Registrar transacción por ajuste de balance (depósito o retiro)
        BigDecimal diff = newBalance.subtract(old);
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            transactionRepository.save(new Transaction(new Money(diff, currency), TransactionType.DEPOSIT, acc, "Balance update"));
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            transactionRepository.save(new Transaction(new Money(diff.abs(), currency), TransactionType.WITHDRAWAL, acc, "Balance update"));
        }
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
        if (acc.getPrimaryOwner() == null) {
            System.err.println("[ERROR] Cuenta Checking sin propietario: ID=" + acc.getId());
            throw new IllegalStateException("Cuenta Checking sin propietario: ID=" + acc.getId());
        }
        // Solo registrar si corresponde aplicar la cuota
        if (acc.shouldApplyMonthlyMaintenanceFee()) {
            var fee = acc.getMonthlyMaintenanceFee();
            acc.applyMonthlyMaintenanceFee();
            Checking saved = checkingRepository.save(acc);
            // Registrar transacción de mantenimiento
            transactionRepository.save(new Transaction(new Money(fee.getAmount(), fee.getCurrencyCode()),
                    TransactionType.MAINTENANCE_FEE, saved, "Monthly maintenance fee"));
            return toDTO(saved);
        }
        return toDTO(acc);
    }

    @Override
    public CheckingResponseDTO checkMinimumBalance(Long accountId) {
        Checking acc = checkingRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Checking no encontrado"));
        boolean below = acc.isBelowMinimumBalance();
        if (below) {
            var penalty = new Money(BigDecimal.valueOf(40), acc.getBalance().getCurrencyCode());
            acc.applyPenaltyIfBelowMinimum();
            Checking saved = checkingRepository.save(acc);
            transactionRepository.save(new Transaction(new Money(penalty.getAmount(), penalty.getCurrencyCode()),
                    TransactionType.PENALTY_FEE, saved, "Penalty fee below minimum"));
            return toDTO(saved);
        }
        return toDTO(acc);
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
